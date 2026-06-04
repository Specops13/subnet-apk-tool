package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class OsiRefFragment : Fragment() {

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        val tv = TextView(requireContext()).apply {
            text = OSI_DATA
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
        private val OSI_DATA = """
OSI MODEL REFERENCE
══════════════════════════════════════════════════

Layer  Name            PDU       Devices / Protocols
─────────────────────────────────────────────────────────
  7    Application     Data      HTTP, HTTPS, FTP, SSH,
                                 DNS, DHCP, SMTP, SNMP
  6    Presentation    Data      TLS/SSL, JPEG, MPEG,
                                 ASCII, encryption
  5    Session         Data      NetBIOS, RPC, SQL,
                                 NFS session mgmt
  4    Transport       Segment   TCP, UDP, SCTP
                       Datagram  Port numbers, flow ctrl,
                                 reliability
  3    Network         Packet    IP, ICMP, IGMP, OSPF,
                                 BGP, Routers, L3 switches
  2    Data Link       Frame     Ethernet, Wi-Fi (802.11),
                                 PPP, HDLC, Switches,
                                 ARP, MAC addresses
  1    Physical        Bit       Cables, hubs, repeaters,
                                 NIC, fiber, DSL, RS-232

TCP/IP MODEL vs OSI
──────────────────────────────────────────────────
TCP/IP Layer    OSI Equivalent
Application     Application + Presentation + Session
Transport       Transport
Internet        Network
Link            Data Link + Physical

IP PROTOCOL NUMBERS (selected)
──────────────────────────────────────────────────
  0   HOPOPT    IPv6 Hop-by-Hop
  1   ICMP      Internet Control Message
  2   IGMP      Internet Group Management
  4   IP-in-IP  IPv4 encapsulation
  6   TCP       Transmission Control
  8   EGP       Exterior Gateway Protocol
  9   IGP       Interior Gateway Protocol
 17   UDP       User Datagram
 41   IPv6      IPv6 encapsulation
 43   IPv6-Route IPv6 Routing header
 44   IPv6-Frag  IPv6 Fragment header
 47   GRE       Generic Routing Encapsulation
 50   ESP       IPSec Encap Security Payload
 51   AH        IPSec Authentication Header
 58   IPv6-ICMP ICMPv6
 59   IPv6-NoNxt No next header
 60   IPv6-Opts  Destination Options
 88   EIGRP     Cisco EIGRP
 89   OSPF      Open Shortest Path First
103   PIM       Protocol Independent Multicast
112   VRRP      Virtual Router Redundancy
115   L2TP      Layer 2 Tunneling Protocol
132   SCTP      Stream Control Transmission
137   MPLS-in-IP MPLS in IP

ROUTING PROTOCOL SUMMARY
──────────────────────────────────────────────────
Protocol  Type    AD    Metric        Scope
RIPv2     DV      120   Hop count     IGP (<=15 hops)
RIPng     DV      120   Hop count     IGP IPv6
OSPF      LS       110   Cost (BW)     IGP
OSPFv3    LS       110   Cost (BW)     IGP IPv6
IS-IS     LS       115   Cost          IGP
EIGRP     Hybrid    90  Composite     IGP (Cisco)
BGP       PV        20  Policy/attr   EGP (internet)
iBGP      PV       200  Policy/attr   Internal BGP

AD = Administrative Distance (lower = preferred)
DV = Distance Vector   LS = Link State
PV = Path Vector

SPANNING TREE (STP) VARIANTS
──────────────────────────────────────────────────
STP  (802.1D)  - Original, 30-50s convergence
RSTP (802.1w)  - Rapid, 1-2s convergence
MSTP (802.1s)  - Multiple instances
PVST+ (Cisco)  - Per-VLAN STP
RPVST+ (Cisco) - Rapid Per-VLAN STP

Port states (RSTP): Discarding → Learning → Forwarding
Port roles: Root, Designated, Alternate, Backup

VLAN TYPES
──────────────────────────────────────────────────
Data VLAN       Carries user traffic
Native VLAN     Untagged on 802.1Q trunk (default 1)
Management VLAN For switch/router management access
Voice VLAN      QoS priority for VoIP
""".trimIndent()
    }
}
