package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentBandwidthBinding

class BandwidthCalcFragment : Fragment() {
    private var _b: FragmentBandwidthBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentBandwidthBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnBwCalc.setOnClickListener { calculate() }
    }

    private fun calculate() {
        b.tvBwError.visibility = View.GONE
        b.cardBwResults.visibility = View.GONE
        val sizeMB  = b.etBwFileSize.text.toString().toDoubleOrNull() ?: run { showError("Invalid file size"); return }
        val speedMbps = b.etBwSpeed.text.toString().toDoubleOrNull() ?: run { showError("Invalid bandwidth"); return }
        val eff = b.etBwEff.text.toString().toDoubleOrNull() ?: 90.0
        if (sizeMB <= 0 || speedMbps <= 0) { showError("Values must be positive"); return }
        try {
            val r = NetworkEngines.calcTransferTime(sizeMB, speedMbps, eff)
            b.tvBwResults.text = buildString {
                appendLine("File Size     : ${"%.2f".format(r.fileSizeMB)} MB  (${"%.2f".format(r.fileSizeMB / 1024)} GB)")
                appendLine("Link Speed    : ${r.bandwidthMbps} Mbps")
                appendLine(r.throughputNote)
                appendLine()
                appendLine("Transfer Time :")
                appendLine("  ${"%.2f".format(r.transferSec)} seconds")
                appendLine("  ${"%.2f".format(r.transferMin)} minutes")
                append("  ${"%.2f".format(r.transferHour)} hours")
            }
            b.cardBwResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Calculation failed") }
    }

    private fun showError(msg: String) { b.tvBwError.text = msg; b.tvBwError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
