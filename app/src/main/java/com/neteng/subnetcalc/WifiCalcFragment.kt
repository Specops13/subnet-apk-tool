package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentWifiBinding

class WifiCalcFragment : Fragment() {
    private var _b: FragmentWifiBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentWifiBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnWifiCalc.setOnClickListener { calculate() }
    }

    private fun calculate() {
        b.tvWifiError.visibility = View.GONE
        b.cardWifiResults.visibility = View.GONE
        val freq  = b.etWifiFreq.text.toString().toDoubleOrNull()    ?: run { showError("Invalid frequency"); return }
        val dist  = b.etWifiDist.text.toString().toDoubleOrNull()    ?: run { showError("Invalid distance"); return }
        val txPow = b.etWifiTxPower.text.toString().toDoubleOrNull() ?: run { showError("Invalid TX power"); return }
        val txGain = b.etWifiTxGain.text.toString().toDoubleOrNull() ?: 0.0
        val rxGain = b.etWifiRxGain.text.toString().toDoubleOrNull() ?: 0.0
        if (freq <= 0 || dist <= 0) { showError("Frequency and distance must be positive"); return }
        try {
            val r = NetworkEngines.calcWifi(freq, dist, txPow, txGain, rxGain)
            val verdictColor = when (r.verdict) {
                "Excellent" -> 0xFF3FB950.toInt()
                "Good"      -> 0xFF58A6FF.toInt()
                "Fair"      -> 0xFFFFA657.toInt()
                else        -> 0xFFF85149.toInt()
            }
            b.tvWifiResults.text = buildString {
                appendLine("Frequency     : $freq GHz")
                appendLine("Distance      : $dist m  (${"%.3f".format(dist / 1000)} km)")
                appendLine()
                appendLine("FSPL          : ${"%.1f".format(r.fsplDb)} dB")
                appendLine("EIRP          : ${"%.1f".format(r.eirpDbm)} dBm  (TX + gain)")
                appendLine("RX Signal     : ${"%.1f".format(r.rxSignalDbm)} dBm")
                appendLine("Link Budget   : ${"%.1f".format(r.linkBudgetDb)} dB")
                appendLine()
                appendLine("Verdict       : ${r.verdict}")
                append(r.note)
            }
            b.tvWifiResults.setTextColor(verdictColor)
            b.cardWifiResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Calculation failed") }
    }

    private fun showError(msg: String) { b.tvWifiError.text = msg; b.tvWifiError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
