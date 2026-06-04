package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentMacToolsBinding

class MacToolsFragment : Fragment() {
    private var _b: FragmentMacToolsBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentMacToolsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnMacAnalyze.setOnClickListener { analyze() }
    }

    private fun analyze() {
        b.tvMacError.visibility = View.GONE
        b.cardMacResults.visibility = View.GONE
        val input = b.etMacInput.text.toString().trim()
        if (input.isEmpty()) { showError("Enter a MAC address"); return }
        try {
            val r = NetworkEngines.analyzeMac(input)
            b.tvMacResults.text = buildString {
                appendLine("Colon format  : ${r.colons}")
                appendLine("Dot format    : ${r.dots}")
                appendLine("Dash format   : ${r.dashes}")
                appendLine("Cisco format  : ${r.cisco}")
                appendLine("OUI           : ${r.oui}")
                appendLine("Multicast     : ${if (r.isMulticast) "Yes (LSB of first octet = 1)" else "No (unicast)"}")
                append("Locally Admin : ${if (r.isLocallyAdministered) "Yes (2nd LSB = 1, LAA)" else "No (globally unique, OUI)"}")
            }
            b.cardMacResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Invalid MAC address") }
    }

    private fun showError(msg: String) { b.tvMacError.text = msg; b.tvMacError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
