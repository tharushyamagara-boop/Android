# Checkpoint Reference: Custom Splash Screen & Forced Dark Mode Fix

**Timestamp**: 2026-08-25T12:07:00+02:00  
**Build Status**: Verified via `./gradlew assembleRelease` (Success)  
**Scope**: Custom Splash Screen (Logo Left, Brand Right) & White Screen Elimination

---

## 1. Modified Files & Purpose

### A. [`app/src/main/java/com/loprok/twa/Application.java`](file:///c:/Users/tharushyamagara/OneDrive%20-%20Water%20For%20People/Desktop/LopRok/App%20Versions/LopRok%20-%20Google%20Play%20package/source/app/src/main/java/com/loprok/twa/Application.java)
- **Change**: `AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);` added to `onCreate()`.
- **Purpose**: Enforces app-wide dark mode at the process level, preventing OS light theme from overriding components.

### B. [`app/src/main/java/com/loprok/twa/LauncherActivity.java`](file:///c:/Users/tharushyamagara/OneDrive%20-%20Water%20For%20People/Desktop/LopRok/App%20Versions/LopRok%20-%20Google%20Play%20package/source/app/src/main/java/com/loprok/twa/LauncherActivity.java)
- **Change**: 
  1. `AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);` in `onCreate()`.
  2. `builder.setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK)` in `launchTwa()`.
  3. `PwaWrapperSplashScreenStrategy` initialized with `@drawable/ic_splash_branded` and `#000000` background passed to `mTwaLauncher.launch()`.
- **Purpose**: Eliminates the white screen during handoff from Android system splash to Chrome TWA.

### C. [`app/src/main/AndroidManifest.xml`](file:///c:/Users/tharushyamagara/OneDrive%20-%20Water%20For%20People/Desktop/LopRok/App%20Versions/LopRok%20-%20Google%20Play%20package/source/app/src/main/AndroidManifest.xml)
- **Change**: `SPLASH_IMAGE_DRAWABLE` updated to `@drawable/ic_splash_branded`.
- **Purpose**: Ensures Chrome displays the identical Logo + Text branded splash drawable while loading web assets.

### D. [`app/src/main/res/values/styles.xml`](file:///c:/Users/tharushyamagara/OneDrive%20-%20Water%20For%20People/Desktop/LopRok/App%20Versions/LopRok%20-%20Google%20Play%20package/source/app/src/main/res/values/styles.xml) & [`app/src/main/res/values-v31/styles.xml`](file:///c:/Users/tharushyamagara/OneDrive%20-%20Water%20For%20People/Desktop/LopRok/App%20Versions/LopRok%20-%20Google%20Play%20package/source/app/src/main/res/values-v31/styles.xml)
- **Change**:
  1. `windowSplashScreenAnimatedIcon` -> `@drawable/ic_splash_branded`.
  2. `windowSplashScreenBackground` -> `#000000`.
  3. `windowSplashScreenIconBackgroundColor` -> `#000000`.
  4. `android:windowLightStatusBar` and `android:windowLightNavigationBar` -> `false`.
  5. `windowIsTranslucent` -> `false` across all themes.
- **Purpose**: System splash displays custom logo + brand typography inside Android 12+ safe zone; all system bars and window backgrounds remain pure black.

### E. Branded Drawable Assets
- **Files**:
  - `app/src/main/res/drawable-mdpi/ic_splash_branded.png` (288x288)
  - `app/src/main/res/drawable-hdpi/ic_splash_branded.png` (432x432)
  - `app/src/main/res/drawable-xhdpi/ic_splash_branded.png` (576x576)
  - `app/src/main/res/drawable-xxhdpi/ic_splash_branded.png` (864x864)
  - `app/src/main/res/drawable-xxxhdpi/ic_splash_branded.png` (1152x1152)
  - `app/src/main/res/drawable/splash_combined.png` (800x200)
- **Purpose**: High-resolution, multi-density vector-aligned PNG assets containing LopRok Logo (left) and "LopRok" text (right).

---

## 2. Deleted / Deprecated Files
- `app/src/main/res/layout/activity_splash.xml` (deleted — unused layout replaced by windowBackground and SplashScreen API).
