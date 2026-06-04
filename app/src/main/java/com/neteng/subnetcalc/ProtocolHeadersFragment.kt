package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProtocolHeadersFragment : Fragment() {

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        val tv = TextView(requireContext()).apply {
            text = HEADERS_DATA
            setTextColor(0xFFE6EDF3.toInt())
            textSize = 11f
            typeface = android.graphics.Typeface.MONOSPACE
            setLineSpacing(0f, 1.3f)
            setPadding(24, 24, 24, 24)
        }
        return ScrollView(requireContext()).apply {
            setBackgroundColor(0xFF0D1117.toInt())
            addView(tv)
        }
    }

    companion object {
        private val HEADERS_DATA = """
PROTOCOL HEADER REFERENCE
══════════════════════════════════════════════════

IPv4 HEADER  (20–60 bytes)
──────────────────────────────────────────────────
 0         1         2         3
 0123456789012345678901234567890123456789
┌────┬────┬────────┬───────────────────┐
│Ver │IHL │  DSCP  │   Total Length    │ 4 bytes
├────┴────┴────────┴───────────────────┤
│ Identification  │Flags│ Frag Offset  │ 4 bytes
├─────────────────┴─────┴──────────────┤
│  TTL  │Protocol │  Header Checksum  │ 4 bytes
├───────┴─────────┴───────────────────┤
│         Source IP Address           │ 4 bytes
├─────────────────────────────────────┤
│       Destination IP Address        │ 4 bytes
└─────────────────────────────────────┘
Options (0–40 bytes if IHL > 5)

Ver=4, IHL=header len/4, Protocol field:
  1=ICMP  6=TCP  17=UDP  41=IPv6-in-IPv4
  47=GRE  50=ESP  51=AH  89=OSPF

IPv6 HEADER  (40 bytes fixed)
──────────────────────────────────────────────────
┌──────┬──────┬────────────────────────┐
│ Ver  │ TC   │     Flow Label         │ 4 bytes
├──────┴──────┴────────────────────────┤
│ Payload Len │ Next Hdr │  Hop Limit  │ 4 bytes
├─────────────┴──────────┴─────────────┤
│         Source Address (128 bit)     │ 16 bytes
├──────────────────────────────────────┤
│       Destination Address (128 bit)  │ 16 bytes
└──────────────────────────────────────┘
Next Header codes same as IPv4 Protocol

TCP HEADER  (20–60 bytes)
──────────────────────────────────────────────────
┌──────────────────┬───────────────────┐
│   Source Port    │    Dest Port      │ 4 bytes
├──────────────────┴───────────────────┤
│          Sequence Number             │ 4 bytes
├──────────────────────────────────────┤
│        Acknowledgment Number         │ 4 bytes
├───────┬──────────┬───────────────────┤
│Data   │ Reserved │C E U A P R S F   │ 4 bytes
│Offset │          │W C R C S S Y I   │
│       │          │R E G K H T N N   │
├───────┴──────────┴───────────────────┤
│   Window Size   │    Checksum        │ 4 bytes
├─────────────────┴────────────────────┤
│  Urgent Pointer │    Options...      │ 4+ bytes
└─────────────────┴────────────────────┘
Flags: SYN ACK FIN RST PSH URG ECE CWR

UDP HEADER  (8 bytes fixed)
──────────────────────────────────────────────────
┌──────────────────┬───────────────────┐
│   Source Port    │    Dest Port      │ 4 bytes
├──────────────────┴───────────────────┤
│     Length       │    Checksum       │ 4 bytes
└──────────────────┴───────────────────┘

ICMP (IPv4) HEADER
──────────────────────────────────────────────────
Type │ Code │ Checksum │ Rest of Header
 0   │  0   │          │ Echo Reply
 3   │ 0-15 │          │ Destination Unreachable
 5   │  0   │          │ Redirect
 8   │  0   │          │ Echo Request
11   │  0   │          │ TTL Exceeded
12   │  0   │          │ Parameter Problem

ICMPv6 TYPES
──────────────────────────────────────────────────
   1  Destination Unreachable
   2  Packet Too Big
   3  Time Exceeded
   4  Parameter Problem
 128  Echo Request
 129  Echo Reply
 133  Router Solicitation (RS)
 134  Router Advertisement (RA)
 135  Neighbor Solicitation (NS)
 136  Neighbor Advertisement (NA)
 137  Redirect

ETHERNET FRAME (DIX/IEEE 802.3)
──────────────────────────────────────────────────
Preamble(7) + SFD(1) + Dst MAC(6) + Src MAC(6)
+ EtherType/Length(2) + Payload(46-1500) + FCS(4)

EtherType values:
  0x0800 IPv4   0x0806 ARP   0x86DD IPv6
  0x8100 802.1Q VLAN         0x8847 MPLS
  0x8848 MPLS mcast          0x88CC LLDP
""".trimIndent()
    }
}
