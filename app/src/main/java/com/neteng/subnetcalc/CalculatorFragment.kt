package com.neteng.subnetcalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentCalculatorBinding
import com.neteng.subnetcalc.databinding.ItemResultRowBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etCidr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculate(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.seekPrefix.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvPrefixLabel.text = "/$progress"
                    val current = binding.etCidr.text.toString()
                    val ip = if ('/' in current) current.substringBefore('/') else current
                    if (ip.isNotBlank()) {
                        binding.etCidr.setText("$ip/$progress")
                        binding.etCidr.setSelection(binding.etCidr.text.length)
                    }
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        binding.btnClear.setOnClickListener {
            binding.etCidr.setText("")
            binding.resultsCard.visibility = View.GONE
        }

        binding.btnExample192.setOnClickListener { binding.etCidr.setText("192.168.1.0/24") }
        binding.btnExample10.setOnClickListener { binding.etCidr.setText("10.0.0.0/8") }
        binding.btnExample172.setOnClickListener { binding.etCidr.setText("172.16.0.0/12") }

        calculate("192.168.1.0/24")
        binding.etCidr.setText("192.168.1.0/24")
    }

    private fun calculate(input: String) {
        if (input.isBlank()) {
            binding.resultsCard.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            return
        }
        try {
            val info = SubnetEngine.calculate(input)
            binding.tvError.visibility = View.GONE
            binding.resultsCard.visibility = View.VISIBLE

            binding.seekPrefix.progress = info.cidrPrefix
            binding.tvPrefixLabel.text = "/${info.cidrPrefix}"

            bind(binding.rowNetwork,   "Network",        info.networkAddress,    info.networkAddress)
            bind(binding.rowBroadcast, "Broadcast",      info.broadcastAddress,  info.broadcastAddress)
            bind(binding.rowMask,      "Subnet Mask",    info.subnetMask,        info.subnetMask)
            bind(binding.rowWildcard,  "Wildcard Mask",  info.wildcardMask,      info.wildcardMask)
            bind(binding.rowFirst,     "First Host",     info.firstHost,         info.firstHost)
            bind(binding.rowLast,      "Last Host",      info.lastHost,          info.lastHost)
            bind(binding.rowHosts,     "Usable Hosts",
                "${fmt(info.usableHosts)} (${fmt(info.totalHosts)} total)", "${info.usableHosts}")
            bind(binding.rowNetBin,    "Network (bin)",  info.networkBinary,     info.networkBinary)
            bind(binding.rowMaskBin,   "Mask (bin)",     info.maskBinary,        info.maskBinary)
            bind(binding.rowClass,     "IP Class",       info.ipClass,           info.ipClass)
            bind(binding.rowType,      "IP Type",        info.ipType,            info.ipType)
            bind(binding.rowReverse,   "Reverse DNS",    info.reversePointer,    info.reversePointer)
            bind(binding.rowSupernet,  "Supernet",       info.supernetCidr,      info.supernetCidr)
            bind(binding.rowIpv6,      "IPv6 Mapped",    info.ipv6MappedAddress, info.ipv6MappedAddress)

            val fraction = info.cidrPrefix.toFloat() / 32f
            binding.subnetBar.progress = (fraction * 100).toInt()
            binding.tvBarLabel.text = "Network bits: ${info.cidrPrefix}  Host bits: ${32 - info.cidrPrefix}"

        } catch (e: Exception) {
            binding.tvError.text = e.message ?: "Invalid input"
            binding.tvError.visibility = View.VISIBLE
            binding.resultsCard.visibility = View.GONE
        }
    }

    private fun bind(row: ItemResultRowBinding, label: String, display: String, copyValue: String) {
        row.root.tag = copyValue
        row.root.setOnLongClickListener {
            (activity as? MainActivity)?.copyToClipboard(label, copyValue)
            true
        }
        // find TextViews by tag set in XML
        row.root.findViewWithTag<android.widget.TextView>("label")?.text = label
        row.root.findViewWithTag<android.widget.TextView>("value")?.text = display
    }

    private fun fmt(n: Long) = "%,d".format(n)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
