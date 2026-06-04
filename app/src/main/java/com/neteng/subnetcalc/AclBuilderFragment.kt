package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentAclBuilderBinding

class AclBuilderFragment : Fragment() {
    private var _b: FragmentAclBuilderBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentAclBuilderBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnAclBuild.setOnClickListener { build() }
    }

    private fun build() {
        b.tvAclError.visibility = View.GONE
        b.cardAclResults.visibility = View.GONE
        val cidr = b.etAclCidr.text.toString().trim()
        if (cidr.isEmpty()) { showError("Enter a CIDR block"); return }
        try {
            val r = NetworkEngines.buildAcl(cidr)
            b.tvAclResults.text = buildString {
                appendLine("Subnet Mask    : ${r.subnetMask}")
                appendLine("Wildcard Mask  : ${r.wildcardMask}")
                appendLine()
                appendLine("── Cisco IOS ──")
                appendLine(r.ciscoPermit)
                appendLine(r.ciscoDeny)
                appendLine()
                appendLine("── Juniper ──")
                appendLine("prefix ${r.juniperPrefix};")
                appendLine()
                appendLine("── Static Route (mask) ──")
                append(r.hostRoute)
            }
            b.cardAclResults.visibility = View.VISIBLE
        } catch (e: Exception) { showError(e.message ?: "Invalid CIDR") }
    }

    private fun showError(msg: String) { b.tvAclError.text = msg; b.tvAclError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
