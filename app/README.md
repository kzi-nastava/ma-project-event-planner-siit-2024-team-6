# Eventure — Quick Start

## Prerequisites

- Android Studio (4.0+)
- Android SDK installed
- Device or emulator
- Make sure that the backend is running before starting an Android project: https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-6

## 1) Get the code

```bash
git clone https://github.com/kzi-nastava/ma-project-event-planner-siit-2024-team-6
cd ma-project-event-planner-siit-2024-team-6
```

Open the project in **Android Studio** and let Gradle sync.

## 2) Configure `local.properties`

At the project root, create or edit `local.properties` and add:

```
sdk.dir=<path-to-your-Android-SDK>
ip_addr=192.168.X.XXX
```

Replace `192.168.X.XXX` with your computer’s current local IP (Windows: `ipconfig` → IPv4 Address).

## 3) Choose a device

- **Phone**: Enable _Developer options_ → _USB debugging_, connect via USB.
- **Emulator**: Create/start one via _Device Manager_.

## 4) Run

Click **Run (▶)** in Android Studio. It will build, install, and launch the app on the selected device.

## If it doesn’t connect to backend

- Update `ip_addr` to your current IP
- Ensure phone/emulator and PC are on the same network
- Verify the backend server is running and reachable
