# MusRoyale Android App

## Overview
This folder contains the Android client for MusRoyale, built with Kotlin and Android Studio. It provides the mobile user interface to connect to the TCP server, join or create games, and play Mus in real time.

## How to run

### Option 1: Android Studio
1. Open the project located in `MusRoyale_AndroidApp/MusRoyaleAndroid/` with Android Studio.
2. Wait for Gradle sync to finish.
3. Select a device (emulator or physical device).
4. Press **Run**.

### Option 2: Command line (Gradle)
From `MusRoyale_AndroidApp/MusRoyaleAndroid/`:

```bash
./gradlew assembleDebug
```

## Configuration
Before running, ensure the app is configured to point to the TCP server:
- Server host: your PC IP / hostname
- Server port: 13000

If you run the server and the Android device on the same machine/emulator:
- Use `10.0.2.2` as host for Android emulator

---

This README is intentionally brief; more detailed protocol/game documentation lives closer to the server implementation.