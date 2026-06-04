package com.neteng.subnetcalc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RadioGroup
import android.widget.TextView
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.neteng.subnetcalc.databinding.FragmentSplitBinding

class SplitFragment : Fragment() {

    private var _binding: FragmentSplitBinding? = null
    private val binding get() = _binding!!
    private var isSplitMode = true

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgMode.setOnCheckedChangeListener { _, checkedId ->
            isSplitMode = (checkedId == R.id.rbSplit)
            updateModeUi()
        }

        binding.btnSplit.setOnClickListener { perform() }

        // Defaults for split mode
        binding.etSplitCidr.setText("192.168.0.0/22")
        binding.etNewPrefix.setText("24")
        updateModeUi()
    }

    private fun updateModeUi() {
        if (isSplitMode) {
            binding.tvCidrLabel.text = "Parent CIDR"
            binding.tvCidrLabel.setTextColor(resources.getColor(R.color.accent_green, null))
            binding.tvPrefixLabel.text = "New subnet prefix (larger = smaller subnets)"
            binding.btnSplit.text = "Split Subnet"
            binding.btnSplit.backgroundTintList =
                resources.getColorStateList(R.color.accent_green_csl, null)
            binding.etSplitCidr.hint = "e.g. 192.168.0.0/22"
            binding.etNewPrefix.hint = "/24"
            binding.cardSupernet.visibility = View.GONE
            binding.tvListHeader.text = "Network              |  First – Last Host      |  Hosts"
        } else {
            binding.tvCidrLabel.text = "Your Network CIDR"
            binding.tvCidrLabel.setTextColor(resources.getColor(R.color.accent_blue, null))
            binding.tvPrefixLabel.text = "Grow to prefix (smaller = larger supernet)"
            binding.btnSplit.text = "Grow Subnet"
            binding.btnSplit.backgroundTintList =
                resources.getColorStateList(R.color.accent_blue_csl, null)
            binding.etSplitCidr.hint = "e.g. 192.168.1.0/24"
            binding.etNewPrefix.hint = "/22"
            binding.tvListHeader.text = "Sibling networks in supernet (◀ YOU = your subnet)"
        }
        // Clear results on mode switch
        binding.listSplitResults.adapter = null
        binding.tvSplitSummary.visibility = View.GONE
        binding.tvSplitError.visibility = View.GONE
    }

    private fun perform() {
        val cidr = binding.etSplitCidr.text.toString().trim()
        val prefixStr = binding.etNewPrefix.text.toString().trim().removePrefix("/")
        binding.tvSplitError.visibility = View.GONE

        try {
            val prefix = prefixStr.toInt()
            if (isSplitMode) doSplit(cidr, prefix) else doGrow(cidr, prefix)
        } catch (e: NumberFormatException) {
            showError("Enter a valid prefix number")
        } catch (e: Exception) {
            showError(e.message ?: "Error")
        }
    }

    private fun doSplit(cidr: String, newPrefix: Int) {
        val subnets = SubnetEngine.splitSubnet(cidr, newPrefix)
        val parentInfo = SubnetEngine.calculate(cidr)
        val count = pow2(newPrefix - parentInfo.cidrPrefix)

        binding.cardSupernet.visibility = View.GONE
        binding.tvSplitSummary.text =
            "/${parentInfo.cidrPrefix} → /$newPrefix: $count subnets" +
            if (count > 256) " (first 256 shown)" else ""
        binding.tvSplitSummary.visibility = View.VISIBLE

        binding.listSplitResults.adapter = SubnetListAdapter(subnets, -1) { s: SubnetSplit ->
            (activity as? MainActivity)?.copyToClipboard("subnet", s.network)
        }
    }

    private fun doGrow(cidr: String, newPrefix: Int) {
        val result = SubnetEngine.growSubnet(cidr, newPrefix)
        val s = result.supernet

        binding.cardSupernet.visibility = View.VISIBLE
        binding.tvSupernetInfo.text =
            "Network:   ${s.networkAddress}/$newPrefix\n" +
            "Mask:      ${s.subnetMask}\n" +
            "Broadcast: ${s.broadcastAddress}\n" +
            "Hosts:     ${"%,d".format(s.usableHosts)} usable\n" +
            "Contains:  ${result.siblings.size} /${SubnetEngine.calculate(cidr).cidrPrefix} subnets"

        val siblingCount = result.siblings.size
        binding.tvSplitSummary.text =
            "Your /${SubnetEngine.calculate(cidr).cidrPrefix} is subnet #${result.currentIndex + 1} of $siblingCount in ${s.networkAddress}/$newPrefix"
        binding.tvSplitSummary.visibility = View.VISIBLE

        binding.listSplitResults.adapter =
            SubnetListAdapter(result.siblings, result.currentIndex) { s: SubnetSplit ->
                (activity as? MainActivity)?.copyToClipboard("subnet", s.network)
            }
    }

    private fun showError(msg: String) {
        binding.tvSplitError.text = msg
        binding.tvSplitError.visibility = View.VISIBLE
        binding.tvSplitSummary.visibility = View.GONE
        binding.listSplitResults.adapter = null
        binding.cardSupernet.visibility = View.GONE
    }

    private fun pow2(exp: Int): Long {
        var result = 1L
        repeat(exp) { result *= 2 }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SubnetListAdapter(
    private val items: List<SubnetSplit>,
    private val highlightIndex: Int,
    private val onLongClick: (SubnetSplit) -> Unit
) : BaseAdapter() {

    override fun getCount() = items.size
    override fun getItem(pos: Int) = items[pos]
    override fun getItemId(pos: Int) = pos.toLong()

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_split_row, parent, false)

        val subnet: SubnetSplit = items[pos]
        val text = view.findViewById<TextView>(R.id.tvSplitItem)
        val tag = view.findViewById<TextView>(R.id.tvSplitTag)

        text.text = "${subnet.network.padEnd(20)}  |  ${subnet.firstHost} – ${subnet.lastHost}  |  ${"%,d".format(subnet.totalHosts)}"

        if (pos == highlightIndex) {
            text.setTextColor(0xFF3FB950.toInt())
            tag.visibility = View.VISIBLE
            view.setBackgroundColor(0xFF1C2A1C.toInt())
        } else {
            text.setTextColor(0xFFE6EDF3.toInt())
            tag.visibility = View.GONE
            view.setBackgroundColor(0xFF161B22.toInt())
        }

        view.setOnLongClickListener { onLongClick(subnet); true }
        return view
    }
}
