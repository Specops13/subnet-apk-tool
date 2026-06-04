package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentLookupBinding

class LookupFragment : Fragment() {

    private var _binding: FragmentLookupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLookupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCheck.setOnClickListener { checkIp() }
        binding.btnConvertMask.setOnClickListener { convertMask() }
    }

    private fun checkIp() {
        val ip = binding.etCheckIp.text.toString().trim()
        val cidr = binding.etCheckCidr.text.toString().trim()
        if (ip.isBlank() || cidr.isBlank()) {
            binding.tvLookupResult.text = "Enter both an IP and a CIDR block."
            return
        }
        try {
            val inSubnet = SubnetEngine.isIpInSubnet(ip, cidr)
            val info = SubnetEngine.calculate(cidr)
            val ipLong = SubnetEngine.ipToLong(ip)
            val offset = ipLong - info.networkAddressLong

            binding.tvLookupResult.text = if (inSubnet) {
                "✓  $ip IS in $cidr\n\nNetwork:   ${info.networkAddress}\nBroadcast: ${info.broadcastAddress}\nOffset:    #$offset (0x${offset.toString(16).uppercase()})"
            } else {
                "✗  $ip is NOT in $cidr\n\nThe network range is ${info.networkAddress} – ${info.broadcastAddress}"
            }
        } catch (e: Exception) {
            binding.tvLookupResult.text = "Error: ${e.message}"
        }
    }

    private fun convertMask() {
        val input = binding.etMaskInput.text.toString().trim()
        try {
            val result = if ('.' in input) {
                // dotted decimal → prefix
                val prefix = SubnetEngine.maskToPrefix(input)
                val wildcard = SubnetEngine.longToIp(SubnetEngine.prefixToMask(prefix).inv() and 0xFFFFFFFFL)
                "/$prefix\nWildcard: $wildcard"
            } else {
                // prefix → dotted decimal
                val prefix = input.removePrefix("/").toInt()
                val mask = SubnetEngine.longToIp(SubnetEngine.prefixToMask(prefix))
                val wildcard = SubnetEngine.longToIp(SubnetEngine.prefixToMask(prefix).inv() and 0xFFFFFFFFL)
                val binary = SubnetEngine.longToBinary(SubnetEngine.prefixToMask(prefix))
                "$mask\nWildcard: $wildcard\nBinary:   $binary"
            }
            binding.tvMaskResult.text = result
        } catch (e: Exception) {
            binding.tvMaskResult.text = "Error: ${e.message}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
