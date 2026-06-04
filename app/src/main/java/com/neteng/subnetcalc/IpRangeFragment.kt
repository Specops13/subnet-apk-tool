package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentIpRangeBinding

class IpRangeFragment : Fragment() {
    private var _b: FragmentIpRangeBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentIpRangeBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnRangeToCidr.setOnClickListener { rangeToCidr() }
        b.btnCidrToRange.setOnClickListener { cidrToRange() }
    }

    private fun rangeToCidr() {
        b.tvRangeError.visibility = View.GONE
        b.cardRangeResults.visibility = View.GONE
        val start = b.etRangeStart.text.toString().trim()
        val end = b.etRangeEnd.text.toString().trim()
        if (start.isEmpty() || end.isEmpty()) { showError("Enter both start and end IP"); return }
        try {
            val cidrs = NetworkEngines.ipRangeToCidr(start, end)
            b.tvRangeResults.text = buildString {
                appendLine("Range: $start – $end")
                appendLine("CIDR blocks (${cidrs.size}):")
                cidrs.forEach { append("  $it\n") }
            }.trimEnd()
            b.cardRangeResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Invalid IP range") }
    }

    private fun cidrToRange() {
        b.tvRangeError.visibility = View.GONE
        b.cardRangeResults.visibility = View.GONE
        val cidr = b.etCidrInput.text.toString().trim()
        if (cidr.isEmpty()) { showError("Enter a CIDR block"); return }
        try {
            val (first, last) = NetworkEngines.cidrToIpRange(cidr)
            b.tvRangeResults.text = "CIDR      : $cidr\nFirst IP  : $first\nLast IP   : $last"
            b.cardRangeResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Invalid CIDR") }
    }

    private fun showError(msg: String) { b.tvRangeError.text = msg; b.tvRangeError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
