package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class PortReferenceFragment : Fragment() {

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        val tv = TextView(requireContext()).apply {
            text = PORT_DATA
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
        private val PORT_DATA = """
TCP/UDP WELL-KNOWN PORT REFERENCE
══════════════════════════════════════════════════

PORT   PROTO  SERVICE
──────────────────────────────────────────────────
   20  TCP    FTP Data
   21  TCP    FTP Control
   22  TCP    SSH / SCP / SFTP
   23  TCP    Telnet
   25  TCP    SMTP
   53  TCP/UDP DNS
   67  UDP    DHCP Server
   68  UDP    DHCP Client
   69  UDP    TFTP
   80  TCP    HTTP
  110  TCP    POP3
  119  TCP    NNTP
  123  UDP    NTP
  137  UDP    NetBIOS Name Service
  138  UDP    NetBIOS Datagram
  139  TCP    NetBIOS Session
  143  TCP    IMAP
  161  UDP    SNMP
  162  UDP    SNMP Trap
  179  TCP    BGP
  389  TCP    LDAP
  443  TCP    HTTPS / TLS
  445  TCP    SMB / CIFS
  465  TCP    SMTPS
  500  UDP    IKE / IPSec
  514  UDP    Syslog
  515  TCP    LPD / LPR Printing
  520  UDP    RIP
  521  UDP    RIPng (IPv6)
  546  UDP    DHCPv6 Client
  547  UDP    DHCPv6 Server
  554  TCP    RTSP
  587  TCP    SMTP Submission
  593  TCP    RPC over HTTP
  636  TCP    LDAPS
  646  TCP    LDP (MPLS)
  750  UDP    Kerberos
  853  TCP    DNS over TLS (DoT)
  873  TCP    rsync
  902  TCP    VMware ESXi
  989  TCP    FTPS Data
  990  TCP    FTPS Control
  993  TCP    IMAPS
  995  TCP    POP3S
 1080  TCP    SOCKS Proxy
 1194  UDP    OpenVPN
 1433  TCP    MS SQL Server
 1434  UDP    MS SQL Browser
 1521  TCP    Oracle DB
 1701  UDP    L2TP
 1720  TCP    H.323
 1723  TCP    PPTP
 1812  UDP    RADIUS Auth
 1813  UDP    RADIUS Acct
 1883  TCP    MQTT
 2049  TCP/UDP NFS
 2181  TCP    ZooKeeper
 2375  TCP    Docker (unencrypted)
 2376  TCP    Docker (TLS)
 3306  TCP    MySQL / MariaDB
 3389  TCP    RDP
 4500  UDP    IPSec NAT-T
 4789  UDP    VXLAN
 5060  TCP/UDP SIP
 5061  TCP    SIPS (TLS)
 5432  TCP    PostgreSQL
 5900  TCP    VNC
 6379  TCP    Redis
 6514  TCP    Syslog TLS
 6881  TCP/UDP BitTorrent
 8080  TCP    HTTP Alternate / Proxy
 8443  TCP    HTTPS Alternate
 8883  TCP    MQTT over TLS
 9000  TCP    SonarQube / PHP-FPM
 9042  TCP    Apache Cassandra
 9092  TCP    Apache Kafka
 9200  TCP    Elasticsearch HTTP
 9300  TCP    Elasticsearch Transport
10250  TCP    Kubernetes kubelet
27017  TCP    MongoDB
49152–65535   Dynamic / Ephemeral ports
""".trimIndent()
    }
}
