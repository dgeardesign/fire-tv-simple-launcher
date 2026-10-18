# Fire TV Simple Launcher

A lightweight Fire TV launcher that strips away Amazon bloat and focuses on simple app launching:
no ads, no recommendations, just a grid of your installed apps.

## Features
- Discovers installed apps (TV and regular launchable apps)
- Grid of icons + names with a clear focus highlight
- D-pad / remote navigation; select launches the app
- **Website shortcuts** mixed into the same grid (blue tile + `LINK` badge); opened in the default browser
- **+ Add website** button; long-press (hold select) a website tile to edit or remove it
- Shortcuts stored in `websites.json` in the app's files dir; seeded with YouTube, Netflix, Google
- Registers as a HOME app (can be chosen as the default launcher)

## Structure
```
app/src/main/java/com/dgeardesign/firetvlauncher/
├── models/  AppInfo, LauncherItem, Website – data for grid tiles
├── data/    AppRepository (apps, browser launch), WebsiteStore (JSON storage)
├── ui/      MainActivity, MainViewModel, AppAdapter
└── utils/   AppFilter, WebsiteLinks – sorting/filtering, URL cleanup, JSON
```

## Requirements
- JDK 17+
- Android SDK (platform 34); set `ANDROID_HOME` or create `local.properties` with `sdk.dir=...`
- A Fire TV device (or Android TV emulator)

## Build
```
./gradlew assembleDebug
./gradlew test
```
The APK is at `app/build/outputs/apk/debug/app-debug.apk`.

## Run on Fire TV
1. On the Fire TV: Settings → My Fire TV → Developer Options → enable **ADB debugging** and **Apps from Unknown Sources**.
2. Find its IP: Settings → My Fire TV → About → Network.
3. From your computer:
   ```
   adb connect <FIRE_TV_IP>:5555
   ./gradlew installDebug
   adb shell am start -n com.dgeardesign.firetvlauncher/.ui.MainActivity
   ```
   The app also shows up under "Your Apps & Channels".

## Notes
- Fire OS typically asks which home app to use when pressing Home after installing a launcher; newer versions
  may ignore third-party launchers. Third-party launcher support varies by Fire OS version/device.
- Back is intentionally ignored on the home screen.

## Ideas for next steps
Hide/reorder apps, favourites row, settings screen, wallpaper, Jetpack Compose for TV migration.
