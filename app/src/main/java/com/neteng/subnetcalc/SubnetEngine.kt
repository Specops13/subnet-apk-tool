package com.neteng.subnetcalc

import java.net.InetAddress
import kotlin.math.pow

data class SubnetInfo(
    val inputCidr: String,
    val networkAddress: String,
    val broadcastAddress: String,
    val subnetMask: String,
    val wildcardMask: String,
    val firstHost: String,
    val lastHost: String,
    val totalHosts: Long,
    val usableHosts: Long,
    val cidrPrefix: Int,
    val ipClass: String,
    val ipType: String,
    val networkBinary: String,
    val maskBinary: String,
    val networkAddressLong: Long,
    val broadcastAddressLong: Long,
    val isPrivate: Boolean,
    val reversePointer: String,
    val supernetCidr: String,
    val ipv6MappedAddress: String
)

data class SubnetSplit(
    val network: String,
    val firstHost: String,
    val lastHost: String,
    val broadcast: String,
    val totalHosts: Long
)

object SubnetEngine {

    fun calculate(cidrInput: String): SubnetInfo {
        val (ipStr, prefixStr) = parseCidr(cidrInput.trim())
        val prefix = prefixStr.toInt()
        require(prefix in 0..32) { "Prefix must be 0-32" }

        val ipLong = ipToLong(ipStr)
        val maskLong = prefixToMask(prefix)
        val wildcardLong = maskLong.inv() and 0xFFFFFFFFL
        val networkLong = ipLong and maskLong
        val broadcastLong = networkLong or wildcardLong
        val firstHostLong = if (prefix < 31) networkLong + 1 else networkLong
        val lastHostLong = if (prefix < 31) broadcastLong - 1 else broadcastLong
        val totalHosts = 2.0.pow((32 - prefix).toDouble()).toLong()
        val usableHosts = if (prefix < 31) maxOf(0L, totalHosts - 2) else totalHosts

        val networkAddr = longToIp(networkLong)
        val broadcastAddr = longToIp(broadcastLong)
        val maskAddr = longToIp(maskLong)
        val wildcardAddr = longToIp(wildcardLong)
        val firstHost = longToIp(firstHostLong)
        val lastHost = longToIp(lastHostLong)

        val octets = ipStr.split(".").map { it.toInt() }
        val firstOctet = octets[0]

        val ipClass = when {
            firstOctet in 1..126 -> "A"
            firstOctet == 127 -> "Loopback"
            firstOctet in 128..191 -> "B"
            firstOctet in 192..223 -> "C"
            firstOctet in 224..239 -> "D (Multicast)"
            firstOctet in 240..254 -> "E (Reserved)"
            firstOctet == 255 -> "Broadcast"
            else -> "Unknown"
        }

        val isPrivate = isPrivateIp(networkLong)
        val ipType = when {
            firstOctet == 127 -> "Loopback"
            firstOctet in 224..239 -> "Multicast"
            firstOctet in 240..255 -> "Reserved"
            isPrivate -> "Private RFC1918"
            isLinkLocal(networkLong) -> "Link-Local"
            isApipa(networkLong) -> "APIPA (169.254.x.x)"
            else -> "Public"
        }

        val networkBinary = longToBinary(networkLong)
        val maskBinary = longToBinary(maskLong)

        val reverseOctets = networkAddr.split(".").reversed()
        val reversePointer = reverseOctets.joinToString(".") + ".in-addr.arpa"

        val supernetPrefix = if (prefix > 0) prefix - 1 else 0
        val supernetMask = prefixToMask(supernetPrefix)
        val supernetNetwork = networkLong and supernetMask
        val supernetCidr = "${longToIp(supernetNetwork)}/$supernetPrefix"

        val ipv6Mapped = "::ffff:$ipStr"

        return SubnetInfo(
            inputCidr = "$networkAddr/$prefix",
            networkAddress = networkAddr,
            broadcastAddress = broadcastAddr,
            subnetMask = maskAddr,
            wildcardMask = wildcardAddr,
            firstHost = firstHost,
            lastHost = lastHost,
            totalHosts = totalHosts,
            usableHosts = usableHosts,
            cidrPrefix = prefix,
            ipClass = ipClass,
            ipType = ipType,
            networkBinary = networkBinary,
            maskBinary = maskBinary,
            networkAddressLong = networkLong,
            broadcastAddressLong = broadcastLong,
            isPrivate = isPrivate,
            reversePointer = reversePointer,
            supernetCidr = supernetCidr,
            ipv6MappedAddress = ipv6Mapped
        )
    }

    fun splitSubnet(cidrInput: String, newPrefix: Int): List<SubnetSplit> {
        val info = calculate(cidrInput)
        require(newPrefix > info.cidrPrefix) { "New prefix must be larger than original" }
        require(newPrefix <= 32) { "Prefix cannot exceed 32" }

        val count = 2.0.pow((newPrefix - info.cidrPrefix).toDouble()).toLong()
        val subnetSize = 2.0.pow((32 - newPrefix).toDouble()).toLong()
        val results = mutableListOf<SubnetSplit>()

        // Limit to 256 results for UI performance
        val limit = minOf(count, 256L)
        for (i in 0 until limit) {
            val netLong = info.networkAddressLong + i * subnetSize
            val bcastLong = netLong + subnetSize - 1
            val firstHostLong = if (newPrefix < 31) netLong + 1 else netLong
            val lastHostLong = if (newPrefix < 31) bcastLong - 1 else bcastLong
            val usable = if (newPrefix < 31) subnetSize - 2 else subnetSize
            results.add(
                SubnetSplit(
                    network = "${longToIp(netLong)}/$newPrefix",
                    firstHost = longToIp(firstHostLong),
                    lastHost = longToIp(lastHostLong),
                    broadcast = longToIp(bcastLong),
                    totalHosts = usable
                )
            )
        }
        return results
    }

    fun isIpInSubnet(ip: String, cidr: String): Boolean {
        val ipLong = ipToLong(ip)
        val info = calculate(cidr)
        return ipLong in info.networkAddressLong..info.broadcastAddressLong
    }

    data class GrowResult(
        val supernet: SubnetInfo,
        val siblings: List<SubnetSplit>,
        val currentIndex: Int
    )

    fun growSubnet(cidrInput: String, newPrefix: Int): GrowResult {
        val info = calculate(cidrInput)
        require(newPrefix < info.cidrPrefix) {
            "Target prefix /$newPrefix must be smaller than /${info.cidrPrefix} to grow the network"
        }
        require(newPrefix >= 0) { "Prefix cannot be negative" }

        val supernetMask = prefixToMask(newPrefix)
        val supernetNetwork = info.networkAddressLong and supernetMask
        val supernetCidr = "${longToIp(supernetNetwork)}/$newPrefix"
        val supernetInfo = calculate(supernetCidr)

        val siblings = splitSubnet(supernetCidr, info.cidrPrefix)
        val currentIndex = siblings.indexOfFirst { it.network == "${info.networkAddress}/${info.cidrPrefix}" }

        return GrowResult(supernet = supernetInfo, siblings = siblings, currentIndex = currentIndex)
    }

    fun commonMasks(): List<Pair<Int, String>> = (0..32).map { prefix ->
        val maskLong = prefixToMask(prefix)
        val hosts = if (prefix < 31) maxOf(0L, 2.0.pow((32 - prefix).toDouble()).toLong() - 2) else 2.0.pow((32 - prefix).toDouble()).toLong()
        prefix to "${longToIp(maskLong)} — $hosts hosts"
    }

    // ─── helpers ───────────────────────────────────────────────────────────────

    private fun parseCidr(input: String): Pair<String, String> {
        return if ('/' in input) {
            val parts = input.split("/")
            require(parts.size == 2) { "Invalid CIDR format" }
            validateIp(parts[0])
            parts[0] to parts[1]
        } else {
            // bare IP — infer class-based default prefix
            validateIp(input)
            val first = input.split(".")[0].toInt()
            val prefix = when {
                first in 1..126 -> "8"
                first in 128..191 -> "16"
                else -> "24"
            }
            input to prefix
        }
    }

    private fun validateIp(ip: String) {
        val parts = ip.split(".")
        require(parts.size == 4) { "Invalid IP address: $ip" }
        parts.forEach {
            val n = it.toIntOrNull() ?: throw IllegalArgumentException("Invalid octet: $it")
            require(n in 0..255) { "Octet out of range: $n" }
        }
    }

    fun ipToLong(ip: String): Long {
        val parts = ip.split(".")
        return (parts[0].toLong() shl 24) or
               (parts[1].toLong() shl 16) or
               (parts[2].toLong() shl 8) or
                parts[3].toLong()
    }

    fun longToIp(long: Long): String {
        return "${(long shr 24) and 0xFF}.${(long shr 16) and 0xFF}.${(long shr 8) and 0xFF}.${long and 0xFF}"
    }

    fun prefixToMask(prefix: Int): Long {
        return if (prefix == 0) 0L else (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
    }

    fun maskToPrefix(mask: String): Int {
        val maskLong = ipToLong(mask)
        var count = 0
        var m = maskLong
        while (m and 0x80000000L != 0L) {
            count++
            m = (m shl 1) and 0xFFFFFFFFL
        }
        return count
    }

    fun longToBinary(long: Long): String {
        val bits = long.toString(2).padStart(32, '0')
        return bits.chunked(8).joinToString(".")
    }

    private fun isPrivateIp(ip: Long): Boolean {
        val a10 = ipToLong("10.0.0.0")
        val a10end = ipToLong("10.255.255.255")
        val a172 = ipToLong("172.16.0.0")
        val a172end = ipToLong("172.31.255.255")
        val a192 = ipToLong("192.168.0.0")
        val a192end = ipToLong("192.168.255.255")
        return ip in a10..a10end || ip in a172..a172end || ip in a192..a192end
    }

    private fun isLinkLocal(ip: Long): Boolean {
        val start = ipToLong("169.254.0.0")
        val end = ipToLong("169.254.255.255")
        return ip in start..end
    }

    private fun isApipa(ip: Long) = isLinkLocal(ip)
}
