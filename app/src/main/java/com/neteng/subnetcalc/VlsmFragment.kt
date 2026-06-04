package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentVlsmBinding

class VlsmFragment : Fragment() {

    private var _b: FragmentVlsmBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentVlsmBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnVlsmCalc.setOnClickListener { calculate() }
    }

    private fun calculate() {
        b.tvVlsmError.visibility = View.GONE
        b.cardVlsmResults.visibility = View.GONE
        val base = b.etVlsmBase.text.toString().trim()
        val reqs = b.etVlsmReqs.text.toString().trim()
        if (base.isEmpty()) { showError("Enter base network"); return }
        if (reqs.isEmpty()) { showError("Enter at least one requirement"); return }

        val requirements = mutableListOf<Pair<String, Int>>()
        for (line in reqs.lines()) {
            val t = line.trim()
            if (t.isEmpty()) continue
            val colon = t.lastIndexOf(':')
            if (colon < 0) { showError("Format: Name:Hosts  (got: $t)"); return }
            val name = t.substring(0, colon).trim()
            val hosts = t.substring(colon + 1).trim().toIntOrNull()
                ?: run { showError("Invalid host count in: $t"); return }
            requirements.add(Pair(name, hosts))
        }
        if (requirements.isEmpty()) { showError("No valid requirements found"); return }

        try {
            val subnets = NetworkEngines.vlsmAllocate(base, requirements)
            val sb = StringBuilder()
            for (s in subnets) {
                sb.appendLine("── ${s.name} (need ${s.required}, got ${s.allocated}) ──")
                if (s.cidr == "OUT OF SPACE") {
                    sb.appendLine("  OUT OF SPACE")
                } else {
                    sb.appendLine("  CIDR      : ${s.cidr}")
                    sb.appendLine("  Network   : ${s.network}")
                    sb.appendLine("  First Host: ${s.firstHost}")
                    sb.appendLine("  Last Host : ${s.lastHost}")
                    sb.appendLine("  Broadcast : ${s.broadcast}")
                }
            }
            b.tvVlsmResults.text = sb.toString().trimEnd()
            b.cardVlsmResults.visibility = View.VISIBLE
        } catch (e: Exception) {
            showError(e.message ?: "Allocation failed")
        }
    }

    private fun showError(msg: String) {
        b.tvVlsmError.text = msg
        b.tvVlsmError.visibility = View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
