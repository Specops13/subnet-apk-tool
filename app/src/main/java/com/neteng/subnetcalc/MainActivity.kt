package com.neteng.subnetcalc

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayoutMediator
import com.neteng.subnetcalc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calculator -> binding.viewPager.currentItem = 0
                R.id.nav_split -> binding.viewPager.currentItem = 1
                R.id.nav_lookup -> binding.viewPager.currentItem = 2
                R.id.nav_cheatsheet -> binding.viewPager.currentItem = 3
            }
            true
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(this, "Copied: $text", Toast.LENGTH_SHORT).show()
    }
}

class MainPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 4
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CalculatorFragment()
        1 -> SplitFragment()
        2 -> LookupFragment()
        3 -> CheatsheetFragment()
        else -> CalculatorFragment()
    }
}
