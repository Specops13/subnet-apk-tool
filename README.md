# SubnetCalc — Android Subnet Calculator

A network engineer's subnet calculator for Android. Works fully offline.

## Features

| Tab | What it does |
|-----|-------------|
| **Calc** | Type any IP/CIDR → network, broadcast, mask, wildcard, first/last host, usable hosts, binary representation, IP class, type (private/public/loopback/APIPA/multicast), reverse DNS pointer, supernet, IPv6-mapped address. Slider to adjust prefix. Long-press any row to copy. |
| **Split ÷ / Grow ↑** | **Split**: divide a parent CIDR into smaller subnets. **Grow**: enter your subnet + a smaller prefix to see the supernet and all sibling networks — your subnet is highlighted with ◀ YOU. |
| **Lookup** | Check if an IP is inside a CIDR block. Convert between dotted mask (255.255.255.0) and prefix (/24) in either direction. |
| **Cheat** | Full CIDR reference /8–/32, RFC 1918 ranges, special addresses, common masks, powers of 2, VLSM design tips. Long-press to copy any row. |

## Install

1. Download `SubnetCalc.apk` from [Releases](../../releases)
2. On your phone: **Settings → Install unknown apps** → allow your file manager/browser
3. Tap the APK to install

Or via ADB: `adb install SubnetCalc.apk`

## Build from source

Requires Java 17 and Android SDK (API 34, build-tools 34.0.0).

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Requirements

- Android 8.0+ (API 26+)
- No internet permission — fully offline
