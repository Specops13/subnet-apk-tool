package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentNumberConvBinding

class NumberConverterFragment : Fragment() {
    private var _b: FragmentNumberConvBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentNumberConvBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.btnNumDec.setOnClickListener { convert(10) }
        b.btnNumHex.setOnClickListener { convert(16) }
        b.btnNumBin.setOnClickListener { convert(2) }
        b.btnNumIp.setOnClickListener  { convertIp() }
    }

    private fun convert(base: Int) {
        b.tvNumError.visibility = View.GONE
        b.cardNumResults.visibility = View.GONE
        val input = b.etNumInput.text.toString().trim()
        if (input.isEmpty()) { showError("Enter a number"); return }
        try {
            val r = NetworkEngines.convertNumber(input, base)
            showResult(r.decimal, r.binary, r.hex, r.octal)
        } catch (e: Exception) { showError("Invalid ${baseName(base)} number: ${e.message}") }
    }

    private fun convertIp() {
        b.tvNumError.visibility = View.GONE
        b.cardNumResults.visibility = View.GONE
        val input = b.etNumInput.text.toString().trim()
        if (input.isEmpty()) { showError("Enter an IP address"); return }
        try {
            val r = NetworkEngines.convertIpToBases(input)
            showResult(r.decimal, r.binary, r.hex, r.octal, input)
        } catch (e: Exception) { showError("Invalid IP address: ${e.message}") }
    }

    private fun showResult(dec: Long, bin: String, hex: String, oct: String, ip: String? = null) {
        b.tvNumResults.text = buildString {
            if (ip != null) appendLine("IP        : $ip")
            appendLine("Decimal   : $dec")
            appendLine("Hex       : 0x$hex")
            appendLine("Binary    : ${bin.padStart(if (dec <= 0xFFFFFFFFL) 32 else 1, '0')}")
            append("Octal     : $oct")
        }
        b.cardNumResults.visibility = View.VISIBLE
    }

    private fun baseName(base: Int) = when (base) { 2 -> "binary"; 16 -> "hex"; else -> "decimal" }
    private fun showError(msg: String) { b.tvNumError.text = msg; b.tvNumError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
