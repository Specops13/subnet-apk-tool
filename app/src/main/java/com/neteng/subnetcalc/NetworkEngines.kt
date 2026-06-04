package com.neteng.subnetcalc

import kotlin.math.log10

object NetworkEngines {

    // ── Route Summarizer ──────────────────────────────────────────────────────

    data class SummaryResult(val cidr: String, val network: Long, val prefix: Int)

    fun summarizeRoutes(cidrs: List<String>): List<SummaryResult> {
        if (cidrs.isEmpty()) return emptyList()
        var networks = cidrs.map { c ->
            val slash = c.lastIndexOf('/')
            val ip = c.substring(0, slash)
            val pfx = c.substring(slash + 1).toInt()
            val net = SubnetEngine.ipToLong(ip) and SubnetEngine.prefixToMask(pfx)
            Pair(net, pfx)
        }.sortedWith(compareBy({ it.first }, { it.second })).toMutableList()

        var changed = true
        while (changed) {
            changed = false
            val merged = mutableListOf<Pair<Long, Int>>()
            var i = 0
            while (i < networks.size) {
                if (i + 1 < networks.size) {
                    val (netA, pfxA) = networks[i]
                    val (netB, pfxB) = networks[i + 1]
                    if (pfxA == pfxB && pfxA > 0) {
                        val parentPfx = pfxA - 1
                        val parentMask = SubnetEngine.prefixToMask(parentPfx)
                        if ((netA and parentMask) == (netB and parentMask)) {
                            merged.add(Pair(netA and parentMask, parentPfx))
                            i += 2; changed = true; continue
                        }
                    }
                }
                merged.add(networks[i]); i++
            }
            networks = merged
        }
        return networks.map { (net, pfx) ->
            SummaryResult("${SubnetEngine.longToIp(net)}/$pfx", net, pfx)
        }
    }

    // ── IP Range to CIDR ──────────────────────────────────────────────────────

    fun ipRangeToCidr(startIp: String, endIp: String): List<String> {
        var start = SubnetEngine.ipToLong(startIp)
        val end = SubnetEngine.ipToLong(endIp)
        val result = mutableListOf<String>()
        while (start <= end) {
            var prefix = 32
            while (prefix > 0) {
                val np = prefix - 1
                val mask = SubnetEngine.prefixToMask(np)
                val blockStart = start and mask
                val blockEnd = blockStart + (1L shl (32 - np)) - 1
                if (blockStart == start && blockEnd <= end) prefix = np else break
            }
            val blockSize = 1L shl (32 - prefix)
            result.add("${SubnetEngine.longToIp(start)}/$prefix")
            start += blockSize
        }
        return result
    }

    fun cidrToIpRange(cidr: String): Pair<String, String> {
        val info = SubnetEngine.calculate(cidr)
        return Pair(info.networkAddress, info.broadcastAddress)
    }

    // ── Overlap Checker ───────────────────────────────────────────────────────

    fun checkOverlap(cidrs: List<String>): List<String> {
        val results = mutableListOf<String>()
        val nets = cidrs.map { c ->
            val slash = c.lastIndexOf('/')
            val ip = c.substring(0, slash)
            val pfx = c.substring(slash + 1).toInt()
            val net = SubnetEngine.ipToLong(ip) and SubnetEngine.prefixToMask(pfx)
            val bcast = net + (1L shl (32 - pfx)) - 1
            Triple(c, net, bcast)
        }
        for (i in nets.indices) {
            for (j in i + 1 until nets.size) {
                val (cA, sA, eA) = nets[i]; val (cB, sB, eB) = nets[j]
                when {
                    sB >= sA && eB <= eA -> results.add("$cB is contained within $cA")
                    sA >= sB && eA <= eB -> results.add("$cA is contained within $cB")
                    sB <= eA && eB >= sA -> results.add("$cA and $cB overlap")
                }
            }
        }
        if (results.isEmpty()) results.add("No overlaps detected")
        return results
    }

    // ── VLSM ──────────────────────────────────────────────────────────────────

    data class VlsmSubnet(
        val name: String, val required: Int, val allocated: Int,
        val cidr: String, val network: String,
        val firstHost: String, val lastHost: String, val broadcast: String
    )

    fun vlsmAllocate(baseNetwork: String, requirements: List<Pair<String, Int>>): List<VlsmSubnet> {
        val sorted = requirements.sortedByDescending { it.second }
        val slash = baseNetwork.lastIndexOf('/')
        val baseIp = baseNetwork.substring(0, slash)
        val basePfx = baseNetwork.substring(slash + 1).toInt()
        var current = SubnetEngine.ipToLong(baseIp) and SubnetEngine.prefixToMask(basePfx)
        val baseEnd = current + (1L shl (32 - basePfx)) - 1

        val result = mutableListOf<VlsmSubnet>()
        for ((name, required) in sorted) {
            var prefix = 32
            while (prefix > 0 && (1L shl (32 - prefix)) - 2 < required) prefix--
            val blockSize = 1L shl (32 - prefix)
            val aligned = ((current + blockSize - 1) / blockSize) * blockSize
            if (aligned + blockSize - 1 > baseEnd) {
                result.add(VlsmSubnet(name, required, 0, "OUT OF SPACE", "-", "-", "-", "-"))
                continue
            }
            val net = aligned; val bcast = aligned + blockSize - 1
            result.add(VlsmSubnet(
                name = name, required = required, allocated = (blockSize - 2).toInt(),
                cidr = "${SubnetEngine.longToIp(net)}/$prefix",
                network = SubnetEngine.longToIp(net),
                firstHost = SubnetEngine.longToIp(net + 1),
                lastHost = SubnetEngine.longToIp(bcast - 1),
                broadcast = SubnetEngine.longToIp(bcast)
            ))
            current = aligned + blockSize
        }
        return result
    }

    // ── Number Converter ──────────────────────────────────────────────────────

    data class NumberResult(val decimal: Long, val binary: String, val hex: String, val octal: String)

    fun convertNumber(input: String, fromBase: Int): NumberResult {
        val clean = input.trim()
            .removePrefix("0x").removePrefix("0X")
            .removePrefix("0b").removePrefix("0B")
        val v = java.lang.Long.parseLong(clean, fromBase)
        return NumberResult(v, v.toString(2), v.toString(16).uppercase(), v.toString(8))
    }

    fun convertIpToBases(ip: String): NumberResult {
        val v = SubnetEngine.ipToLong(ip)
        return NumberResult(v, SubnetEngine.longToBinary(v), "%08X".format(v), v.toString(8))
    }

    // ── MAC Tools ─────────────────────────────────────────────────────────────

    data class MacInfo(
        val colons: String, val dots: String, val dashes: String, val cisco: String,
        val oui: String, val isMulticast: Boolean, val isLocallyAdministered: Boolean
    )

    fun analyzeMac(input: String): MacInfo {
        val clean = input.replace("[:\\-.]".toRegex(), "").uppercase()
        require(clean.length == 12 && clean.all { it in "0123456789ABCDEF" }) {
            "Invalid MAC: expected 12 hex digits"
        }
        val bytes = clean.chunked(2).map { it.toInt(16) }
        return MacInfo(
            colons = clean.chunked(2).joinToString(":"),
            dots = clean.chunked(4).joinToString("."),
            dashes = clean.chunked(2).joinToString("-"),
            cisco = clean.chunked(4).joinToString(".").lowercase(),
            oui = clean.take(6),
            isMulticast = (bytes[0] and 1) == 1,
            isLocallyAdministered = (bytes[0] and 2) == 2
        )
    }

    // ── ACL / Wildcard Builder ────────────────────────────────────────────────

    data class AclInfo(
        val subnetMask: String, val wildcardMask: String,
        val ciscoPermit: String, val ciscoDeny: String,
        val juniperPrefix: String, val hostRoute: String
    )

    fun buildAcl(cidr: String): AclInfo {
        val info = SubnetEngine.calculate(cidr)
        return AclInfo(
            subnetMask = info.subnetMask,
            wildcardMask = info.wildcardMask,
            ciscoPermit = "permit ip ${info.networkAddress} ${info.wildcardMask}",
            ciscoDeny = "deny   ip ${info.networkAddress} ${info.wildcardMask}",
            juniperPrefix = "${info.networkAddress}/${info.cidrPrefix}",
            hostRoute = "${info.networkAddress} ${info.subnetMask}"
        )
    }

    // ── Bandwidth / Transfer ──────────────────────────────────────────────────

    data class BandwidthResult(
        val fileSizeMB: Double, val bandwidthMbps: Double,
        val transferSec: Double, val transferMin: Double, val transferHour: Double,
        val throughputNote: String
    )

    fun calcTransferTime(
        fileSizeMB: Double,
        bandwidthMbps: Double,
        efficiencyPct: Double = 90.0
    ): BandwidthResult {
        val eff = efficiencyPct / 100.0
        val effective = bandwidthMbps * eff
        val secs = (fileSizeMB * 8.0) / effective
        return BandwidthResult(
            fileSizeMB, bandwidthMbps, secs, secs / 60.0, secs / 3600.0,
            "Effective: ${"%.1f".format(effective)} Mbps @ ${efficiencyPct.toInt()}% efficiency"
        )
    }

    // ── MTU / Tunnel Overhead ─────────────────────────────────────────────────

    data class MtuResult(
        val baseMtu: Int, val tunnelType: String,
        val overhead: Int, val effectiveMtu: Int, val breakdown: String
    )

    fun calcMtu(baseMtu: Int, tunnelType: String): MtuResult {
        val (oh, detail) = when (tunnelType.uppercase()) {
            "GRE"       -> 24 to "20 (outer IP) + 4 (GRE header)"
            "GRE+IPSEC" -> 58 to "20 IP + 4 GRE + 8 ESP hdr + 12 ICV + 14 pad"
            "IPSEC_ESP" -> 50 to "20 (outer IP) + 8 (ESP hdr) + 10 (pad) + 12 (ICV)"
            "IPSEC_AH"  -> 24 to "20 (outer IP) + 24 (AH header)"
            "VXLAN"     -> 50 to "14 (outer Eth) + 20 (outer IP) + 8 (UDP) + 8 (VXLAN)"
            "MPLS"      ->  4 to "4 bytes per MPLS label"
            "MPLS_2"    ->  8 to "8 bytes (two MPLS labels)"
            "PPPOE"     ->  8 to "6 (PPPoE header) + 2 (PPP)"
            "L2TP"      -> 38 to "20 (outer IP) + 8 (UDP) + 10 (L2TP)"
            "IPIP"      -> 20 to "20 bytes outer IPv4 header"
            "SIT_6IN4"  -> 20 to "20 bytes outer IPv4 header (6in4 tunnel)"
            else        ->  0 to "No encapsulation overhead"
        }
        return MtuResult(baseMtu, tunnelType, oh, baseMtu - oh, detail)
    }

    // ── PPS Calculator ────────────────────────────────────────────────────────

    data class PpsResult(
        val linkGbps: Double, val frameSizeBytes: Int,
        val pps: Double, val mpps: Double
    )

    fun calcPps(bandwidthGbps: Double, frameSizeBytes: Int): PpsResult {
        val wireBytes = frameSizeBytes + 20  // +8 preamble/SFD +12 IFG
        val bps = bandwidthGbps * 1_000_000_000.0
        val pps = bps / (wireBytes * 8.0)
        return PpsResult(bandwidthGbps, frameSizeBytes, pps, pps / 1_000_000.0)
    }

    // ── WiFi / RF ─────────────────────────────────────────────────────────────

    data class WifiResult(
        val fsplDb: Double, val eirpDbm: Double, val rxSignalDbm: Double,
        val linkBudgetDb: Double, val verdict: String, val note: String
    )

    fun calcWifi(
        frequencyGhz: Double, distanceM: Double,
        txPowerDbm: Double, txGainDbi: Double, rxGainDbi: Double,
        cableLossDb: Double = 0.0
    ): WifiResult {
        val distKm = distanceM / 1000.0
        val fspl = 20.0 * log10(distKm) + 20.0 * log10(frequencyGhz) + 92.45
        val eirp = txPowerDbm + txGainDbi - cableLossDb
        val rx = eirp - fspl + rxGainDbi
        val lb = eirp + rxGainDbi - fspl
        val (verdict, note) = when {
            rx >= -65 -> "Excellent" to "Strong — HD streaming capable"
            rx >= -75 -> "Good"      to "Reliable — normal operation"
            rx >= -80 -> "Fair"      to "Usable — may have intermittent drops"
            rx >= -90 -> "Poor"      to "Marginal — likely disconnects"
            else      -> "No Link"   to "Below sensitivity threshold"
        }
        return WifiResult(fspl, eirp, rx, lb, verdict, note)
    }
}
