package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentPpsBinding

class PpsCalcFragment : Fragment() {
    private var _b: FragmentPpsBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentPpsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnPps64.setOnClickListener   { b.etPpsFrameSize.setText("64") }
        b.btnPps512.setOnClickListener  { b.etPpsFrameSize.setText("512") }
        b.btnPps1518.setOnClickListener { b.etPpsFrameSize.setText("1518") }
        b.btnPpsCalc.setOnClickListener { calculate() }
    }

    private fun calculate() {
        b.tvPpsError.visibility = View.GONE
        b.cardPpsResults.visibility = View.GONE
        val gbps = b.etPpsGbps.text.toString().toDoubleOrNull() ?: run { showError("Invalid link speed"); return }
        val frame = b.etPpsFrameSize.text.toString().toIntOrNull() ?: run { showError("Invalid frame size"); return }
        if (gbps <= 0 || frame < 64) { showError("Speed must be > 0, frame size >= 64"); return }
        val r = NetworkEngines.calcPps(gbps, frame)
        b.tvPpsResults.text = buildString {
            appendLine("Link Speed     : ${r.linkGbps} Gbps")
            appendLine("Frame Size     : ${r.frameSizeBytes} bytes")
            appendLine("Wire Size      : ${r.frameSizeBytes + 20} bytes (+ 20 preamble/IFG)")
            appendLine()
            appendLine("PPS            : ${"%.0f".format(r.pps)}")
            append("MPPS           : ${"%.3f".format(r.mpps)}")
        }
        b.cardPpsResults.visibility = View.VISIBLE
    }

    private fun showError(msg: String) { b.tvPpsError.text = msg; b.tvPpsError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
