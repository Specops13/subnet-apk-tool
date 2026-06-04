package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentMtuBinding

class MtuCalcFragment : Fragment() {
    private var _b: FragmentMtuBinding? = null
    private val b get() = _b!!

    private val tunnelTypes = listOf(
        "NONE", "GRE", "GRE+IPSEC", "IPSEC_ESP", "IPSEC_AH",
        "VXLAN", "MPLS", "MPLS_2", "PPPOE", "L2TP", "IPIP", "SIT_6IN4"
    )

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentMtuBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tunnelTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.spinMtuType.adapter = adapter
        b.btnMtuCalc.setOnClickListener { calculate() }
    }

    private fun calculate() {
        b.cardMtuResults.visibility = View.GONE
        val base = b.etMtuBase.text.toString().toIntOrNull() ?: 1500
        val type = tunnelTypes[b.spinMtuType.selectedItemPosition]
        val r = NetworkEngines.calcMtu(base, type)
        b.tvMtuResults.text = buildString {
            appendLine("Base MTU      : ${r.baseMtu} bytes")
            appendLine("Tunnel Type   : ${r.tunnelType}")
            appendLine("Overhead      : ${r.overhead} bytes")
            appendLine("Effective MTU : ${r.effectiveMtu} bytes")
            appendLine()
            append("Overhead breakdown:\n  ${r.breakdown}")
        }
        b.cardMtuResults.visibility = View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
