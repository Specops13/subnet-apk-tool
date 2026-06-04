package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class MulticastRefFragment : Fragment() {

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        val tv = TextView(requireContext()).apply {
            text = MCAST_DATA
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
        private val MCAST_DATA = """
MULTICAST REFERENCE
══════════════════════════════════════════════════

IPv4 MULTICAST RANGES (224.0.0.0/4)
──────────────────────────────────────────────────
224.0.0.0/24    Link-local (TTL=1, not routed)
224.0.0.1       All hosts on segment
224.0.0.2       All routers on segment
224.0.0.4       DVMRP routers
224.0.0.5       OSPF all routers (AllSPFRouters)
224.0.0.6       OSPF DR/BDR (AllDRouters)
224.0.0.9       RIPv2 routers
224.0.0.10      EIGRP routers
224.0.0.13      PIM routers
224.0.0.18      VRRP
224.0.0.19-21   IS-IS over IP
224.0.0.22      IGMP v3
224.0.0.102     HSRPv2 / GLBP

224.0.1.0/24    Internetwork control
224.0.1.1       NTP

232.0.0.0/8     Source-Specific Multicast (SSM)
233.0.0.0/8     GLOP (AS-based, RFC 3180)
234.0.0.0/8     Unicast-Prefix-based (RFC 6034)
239.0.0.0/8     Organization-local scope

ETHERNET MULTICAST MAPPING
──────────────────────────────────────────────────
IPv4 → MAC:  01:00:5E:xx:xx:xx
  Lower 23 bits of IP group → lower 23 bits of MAC
  e.g. 225.1.2.3 → 01:00:5E:01:02:03

IPv6 → MAC:  33:33:xx:xx:xx:xx
  Last 32 bits of IPv6 address → last 4 octets

IGMP VERSIONS
──────────────────────────────────────────────────
IGMPv1  - Join only, router queries
IGMPv2  - Leave group message added
IGMPv3  - Source-specific multicast (SSM)

IGMP MESSAGE TYPES
  0x11  Membership Query
  0x12  IGMPv1 Report
  0x16  IGMPv2 Report
  0x17  Leave Group
  0x22  IGMPv3 Report

IPv6 MULTICAST ADDRESSES
──────────────────────────────────────────────────
ff02::1     All nodes (link-local)
ff02::2     All routers (link-local)
ff02::5     OSPFv3 all routers
ff02::6     OSPFv3 DR routers
ff02::9     RIPng routers
ff02::a     EIGRP routers
ff02::d     PIM routers
ff02::16    MLDv2 routers
ff02::1:2   All DHCP agents
ff05::2     All routers (site-local)
ff05::1:3   All DHCP servers (site)

Solicited-Node Multicast:
  ff02::1:ff00:0/104 + last 24 bits of address
  Used for Neighbor Discovery (replaces ARP)

MLD VERSIONS
──────────────────────────────────────────────────
MLDv1  - ICMPv6 type 130/131/132
MLDv2  - ICMPv6 type 143 (SSM support)

PIM MODES
──────────────────────────────────────────────────
PIM-DM   Dense Mode — flood & prune
PIM-SM   Sparse Mode — explicit join, RP required
PIM-SSM  Source-Specific — no RP, RFC 4607
BIDIR    Bidirectional PIM — shared tree only
""".trimIndent()
    }
}
