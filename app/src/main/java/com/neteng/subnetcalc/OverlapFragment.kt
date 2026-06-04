package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentOverlapBinding

class OverlapFragment : Fragment() {
    private var _b: FragmentOverlapBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentOverlapBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnCheckOverlap.setOnClickListener { check() }
    }

    private fun check() {
        b.tvOverlapError.visibility = View.GONE
        b.cardOverlapResults.visibility = View.GONE
        val cidrs = b.etOverlapCidrs.text.toString().lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (cidrs.size < 2) { showError("Enter at least two CIDRs"); return }
        try {
            val results = NetworkEngines.checkOverlap(cidrs)
            val hasOverlap = results.any { !it.startsWith("No overlap") }
            b.tvOverlapResults.text = results.joinToString("\n")
            b.tvOverlapResults.setTextColor(
                if (hasOverlap) 0xFFFFA657.toInt() else 0xFF3FB950.toInt()
            )
            b.cardOverlapResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Check failed") }
    }

    private fun showError(msg: String) { b.tvOverlapError.text = msg; b.tvOverlapError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
