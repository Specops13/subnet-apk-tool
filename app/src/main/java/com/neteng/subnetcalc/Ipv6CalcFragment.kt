package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentIpv6CalcBinding

class Ipv6CalcFragment : Fragment() {
    private var _b: FragmentIpv6CalcBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentIpv6CalcBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnIpv6Calc.setOnClickListener { calculate() }
    }

    private fun calculate() {
        val input = b.etIpv6Input.text.toString().trim()
        b.tvIpv6Error.visibility = View.GONE
        b.cardIpv6Results.visibility = View.GONE
        if (input.isEmpty()) { showError("Enter an IPv6 address"); return }
        try {
            val r = Ipv6Engine.calculate(input)
            b.tvIpv6Results.text = buildString {
                appendLine("Compressed    : ${r.compressedAddress}")
                appendLine("Expanded      : ${r.expandedAddress}")
                appendLine("Prefix Length : /${r.prefixLength}")
                appendLine("Network       : ${r.networkCompressed}")
                appendLine("First Host    : ${r.firstHost}")
                appendLine("Last Host     : ${r.lastHost}")
                appendLine("Last Address  : ${r.lastAddress}")
                appendLine("Total Addrs   : ${r.totalAddresses}")
                appendLine("Type          : ${r.addressType}")
                appendLine("Solicited-Node: ${r.solicitedNodeMulticast}")
                append("Reverse DNS   : ${r.reverseDnsZone}")
            }
            b.cardIpv6Results.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Invalid IPv6 input") }
    }

    private fun showError(msg: String) { b.tvIpv6Error.text = msg; b.tvIpv6Error.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
