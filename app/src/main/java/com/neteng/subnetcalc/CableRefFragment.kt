package com.neteng.subnetcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class CableRefFragment : Fragment() {

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        val tv = TextView(requireContext()).apply {
            text = CABLE_DATA
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
        private val CABLE_DATA = """
CABLE & CONNECTOR REFERENCE
══════════════════════════════════════════════════

ETHERNET STANDARDS
──────────────────────────────────────────────────
Standard     Speed     Cable         Max Distance
10BASE-T     10 Mbps   Cat 3+        100 m
100BASE-TX   100 Mbps  Cat 5+        100 m
1000BASE-T   1 Gbps    Cat 5e+       100 m
1000BASE-SX  1 Gbps    MMF 850nm     550 m
1000BASE-LX  1 Gbps    SMF/MMF       10 km
2.5GBASE-T   2.5 Gbps  Cat 5e+       100 m
5GBASE-T     5 Gbps    Cat 6+        100 m
10GBASE-T    10 Gbps   Cat 6A+       100 m
10GBASE-SR   10 Gbps   MMF 850nm     300-400 m
10GBASE-LR   10 Gbps   SMF 1310nm    10 km
10GBASE-ER   10 Gbps   SMF 1550nm    40 km
25GBASE-SR   25 Gbps   MMF 850nm     100 m
40GBASE-SR4  40 Gbps   MMF 4x lane   150 m
100GBASE-SR4 100 Gbps  MMF 4x lane   100 m
400GBASE-SR8 400 Gbps  MMF 8x lane   100 m

TWISTED PAIR CATEGORIES
──────────────────────────────────────────────────
Cat 3   16 MHz     10BASE-T, POTS
Cat 5   100 MHz    100BASE-TX (obsolete)
Cat 5e  100 MHz    1000BASE-T  (most common)
Cat 6   250 MHz    1000BASE-T, 10G to 55m
Cat 6A  500 MHz    10GBASE-T to 100m
Cat 7   600 MHz    10G, shielded (S/FTP)
Cat 8   2000 MHz   25/40G, data centers

T568A PINOUT (RJ-45)
──────────────────────────────────────────────────
Pin 1: White/Green    Tx+
Pin 2: Green          Tx-
Pin 3: White/Orange   Rx+
Pin 4: Blue
Pin 5: White/Blue
Pin 6: Orange         Rx-
Pin 7: White/Brown
Pin 8: Brown

T568B PINOUT (RJ-45)  ← most common in US
──────────────────────────────────────────────────
Pin 1: White/Orange   Tx+
Pin 2: Orange         Tx-
Pin 3: White/Green    Rx+
Pin 4: Blue
Pin 5: White/Blue
Pin 6: Green          Rx-
Pin 7: White/Brown
Pin 8: Brown

Straight-through: both ends T568B (or both T568A)
Crossover: one end T568A, other T568B
(Modern switches use Auto-MDIX — crossover not needed)

FIBER TYPES
──────────────────────────────────────────────────
MMF (Multimode)
  OM1  62.5/125 µm  Orange  200 MHz·km
  OM2  50/125 µm    Orange  500 MHz·km
  OM3  50/125 µm    Aqua    2000 MHz·km  (10G to 300m)
  OM4  50/125 µm    Aqua    4700 MHz·km  (10G to 550m)
  OM5  50/125 µm    Lime    28000 MHz·km (SWDM4)

SMF (Single-mode)
  OS1  9/125 µm     Yellow  Indoor, tight buffer
  OS2  9/125 µm     Yellow  Outdoor, loose tube

FIBER CONNECTORS
──────────────────────────────────────────────────
LC    Small form factor, most common in data centers
SC    Square connector, push-pull
ST    Bayonet, older installations
FC    Screw-on, high-vibration
MTP/MPO  Multi-fiber, 12/24 strand, 40G/100G

TRANSCEIVER FORM FACTORS
──────────────────────────────────────────────────
SFP    1 Gbps    Small Form-factor Pluggable
SFP+   10 Gbps   Enhanced SFP
SFP28  25 Gbps
QSFP+  40 Gbps   Quad SFP+
QSFP28 100 Gbps
QSFP-DD 400 Gbps Double Density
CFP    100 Gbps  C Form-factor Pluggable

CONSOLE / SERIAL
──────────────────────────────────────────────────
RJ-45 to DB-9 (Cisco rollover cable):
  RJ-45 Pin 1 → DB-9 Pin 8 (CTS)
  RJ-45 Pin 2 → DB-9 Pin 6 (DSR)
  RJ-45 Pin 3 → DB-9 Pin 2 (RxD)
  RJ-45 Pin 4 → DB-9 Pin 5 (GND)
  RJ-45 Pin 5 → DB-9 Pin 5 (GND)
  RJ-45 Pin 6 → DB-9 Pin 3 (TxD)
  RJ-45 Pin 7 → DB-9 Pin 4 (DTR)
  RJ-45 Pin 8 → DB-9 Pin 7 (RTS)

Default console settings: 9600 8N1, no flow control
""".trimIndent()
    }
}
