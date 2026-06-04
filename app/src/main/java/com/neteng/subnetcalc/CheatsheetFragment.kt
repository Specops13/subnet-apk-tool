package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentCheatsheetBinding

class CheatsheetFragment : Fragment() {

    private var _binding: FragmentCheatsheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheatsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cheatRows = buildCheatsheet()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_cheat_row, R.id.tvCheatItem, cheatRows)
        binding.listCheatsheet.adapter = adapter

        binding.listCheatsheet.setOnItemLongClickListener { _, _, pos, _ ->
            (activity as? MainActivity)?.copyToClipboard("mask", cheatRows[pos])
            true
        }
    }

    private fun buildCheatsheet(): List<String> {
        val rows = mutableListOf<String>()
        rows += "── CIDR Reference ──────────────────────────"
        rows += "Prefix  Subnet Mask        Hosts     Blocks"
        rows += "────────────────────────────────────────────"

        for (prefix in 8..32) {
            val maskLong = SubnetEngine.prefixToMask(prefix)
            val mask = SubnetEngine.longToIp(maskLong)
            val total = Math.pow(2.0, (32 - prefix).toDouble()).toLong()
            val usable = if (prefix < 31) maxOf(0L, total - 2) else total
            val blocks = if (prefix >= 24) "${Math.pow(2.0, (prefix - 24).toDouble()).toLong()}×/32" else ""
            rows += "/$prefix  ${mask.padEnd(17)} ${"%-10s".format("%,d".format(usable))} $blocks"
        }

        rows += ""
        rows += "── RFC 1918 Private Ranges ──────────────────"
        rows += "10.0.0.0/8       — Class A  16,777,216 hosts"
        rows += "172.16.0.0/12    — Class B   1,048,576 hosts"
        rows += "192.168.0.0/16   — Class C      65,536 hosts"

        rows += ""
        rows += "── Special Addresses ────────────────────────"
        rows += "127.0.0.0/8      — Loopback"
        rows += "169.254.0.0/16   — APIPA / Link-Local"
        rows += "224.0.0.0/4      — Multicast"
        rows += "240.0.0.0/4      — Reserved (Class E)"
        rows += "0.0.0.0          — Default route / any"
        rows += "255.255.255.255  — Limited broadcast"

        rows += ""
        rows += "── Common Masks Quick Ref ───────────────────"
        rows += "/30  255.255.255.252  2 hosts   (P2P links)"
        rows += "/29  255.255.255.248  6 hosts"
        rows += "/28  255.255.255.240  14 hosts"
        rows += "/27  255.255.255.224  30 hosts"
        rows += "/26  255.255.255.192  62 hosts"
        rows += "/25  255.255.255.128  126 hosts"
        rows += "/24  255.255.255.0    254 hosts  (Class C)"
        rows += "/23  255.255.254.0    510 hosts"
        rows += "/22  255.255.252.0    1022 hosts"
        rows += "/21  255.255.248.0    2046 hosts"
        rows += "/20  255.255.240.0    4094 hosts"
        rows += "/19  255.255.224.0    8190 hosts"
        rows += "/18  255.255.192.0    16382 hosts"
        rows += "/17  255.255.128.0    32766 hosts"
        rows += "/16  255.255.0.0      65534 hosts  (Class B)"
        rows += "/8   255.0.0.0        16,777,214 h (Class A)"

        rows += ""
        rows += "── Powers of 2 ──────────────────────────────"
        rows += "2^1=2   2^2=4   2^3=8   2^4=16"
        rows += "2^5=32  2^6=64  2^7=128  2^8=256"
        rows += "2^9=512 2^10=1024 2^11=2048 2^12=4096"
        rows += "2^16=65536  2^24=16,777,216  2^32=4,294,967,296"

        rows += ""
        rows += "── VLSM Design Tips ─────────────────────────"
        rows += "1. Sort subnets largest → smallest"
        rows += "2. Allocate on power-of-2 boundaries"
        rows += "3. Use /30 or /31 for point-to-point"
        rows += "4. Use /32 for loopback addresses"
        rows += "5. Leave gaps for future growth"

        return rows
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
