package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentRouteSummarizerBinding

class RouteSummarizerFragment : Fragment() {
    private var _b: FragmentRouteSummarizerBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRouteSummarizerBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnSummarize.setOnClickListener { calculate() }
    }

    private fun calculate() {
        b.tvSumError.visibility = View.GONE
        b.cardSumResults.visibility = View.GONE
        val cidrs = b.etSumRoutes.text.toString().lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (cidrs.isEmpty()) { showError("Enter at least one CIDR route"); return }
        try {
            val results = NetworkEngines.summarizeRoutes(cidrs)
            b.tvSumResults.text = buildString {
                appendLine("Input  (${cidrs.size} routes):")
                cidrs.forEach { appendLine("  $it") }
                appendLine()
                appendLine("Summary (${results.size} route${if (results.size == 1) "" else "s"}):")
                results.forEach { append("  ${it.cidr}\n") }
            }.trimEnd()
            b.cardSumResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Summarization failed") }
    }

    private fun showError(msg: String) { b.tvSumError.text = msg; b.tvSumError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
