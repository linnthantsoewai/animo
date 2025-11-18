# Installing Animo on Other Devices

This guide explains how to install the Animo app on other Android devices.

## Prerequisites
- Android device running Android 7.0 (API 24) or higher
- USB cable (for direct installation) OR
- Access to the APK file (for wireless installation)

---

## Method 1: Build and Install APK (Recommended)

### Step 1: Build the APK
From your project directory, run:

```bash
cd /Users/linn/AndroidStudioProjects/animo
./gradlew assembleRelease
```

The APK will be created at:
```
app/build/outputs/apk/release/app-release.apk
```

### Step 2: Install on Device

#### Option A: Via USB Cable
1. Enable USB debugging on your Android device:
   - Go to **Settings** → **About Phone**
   - Tap **Build Number** 7 times to enable Developer Options
   - Go to **Settings** → **Developer Options**
   - Enable **USB Debugging**

2. Connect device via USB cable

3. Install the APK:
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

#### Option B: Via File Transfer (No USB Debugging Required)
1. Copy the APK file to your device (email, cloud storage, etc.)
2. On the device, locate the APK file using a file manager
3. Tap the APK to install
4. If prompted, enable "Install from Unknown Sources" for your file manager

#### Option C: Via WiFi (ADB Over WiFi)
1. Connect both computer and device to the same WiFi network
2. On the device, enable USB debugging and connect via USB once
3. Run:
```bash
adb tcpip 5555
adb connect <device-ip-address>:5555
```
4. Disconnect USB cable and install:
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

---

## Method 2: Share APK File

### Build the APK:
```bash
./gradlew assembleRelease
```

### Share the file at:
```
app/build/outputs/apk/release/app-release.apk
```

You can share this file via:
- **Email** - Attach the APK and send to the recipient
- **Google Drive / Dropbox** - Upload and share the link
- **Messaging apps** - WhatsApp, Telegram, etc.
- **Direct transfer** - AirDrop, Bluetooth, etc.

### Installation on Recipient's Device:
1. Download the APK file
2. Open the APK file
3. Allow installation from unknown sources if prompted
4. Tap "Install"

---

## Method 3: Google Play Console (For Distribution)

### For wider distribution:
1. Create a signed APK or App Bundle:
```bash
./gradlew bundleRelease
```

2. Upload to Google Play Console
3. Users can download from Play Store

---

## Method 4: Install via Android Studio

1. Open the project in Android Studio
2. Connect target device via USB
3. Click the **Run** button (▶️) or press **Cmd + R** (Mac) / **Ctrl + R** (Windows)
4. Select your target device from the list
5. Android Studio will build and install automatically

---

## Troubleshooting

### "App not installed" error
- Make sure the device has enough storage space
- Uninstall any previous version of the app
- Clear the download cache

### "Install from Unknown Sources" blocked
- Go to **Settings** → **Security**
- Enable "Unknown Sources" or "Install Unknown Apps"
- Allow installation for your file manager/browser

### ADB not found
- Install Android SDK Platform Tools
- Add to PATH: `/Users/linn/Library/Android/sdk/platform-tools`

### Device not detected
```bash
# Check if device is connected
adb devices

# Restart ADB server
adb kill-server
adb start-server
```

---

## Quick Commands Cheat Sheet

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Install release APK
adb install app/build/outputs/apk/release/app-release.apk

# Install and replace existing app
adb install -r app/build/outputs/apk/release/app-release.apk

# Uninstall app
adb uninstall com.example.animo

# List connected devices
adb devices

# Install on specific device
adb -s <device-id> install app/build/outputs/apk/release/app-release.apk
```

---

## App Details
- **Package Name**: com.example.animo
- **Minimum Android Version**: 7.0 (API 24)
- **Target Android Version**: 14.0 (API 36)
- **Version**: 1.0
- **Version Code**: 1

