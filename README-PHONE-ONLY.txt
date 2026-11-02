SOUNDTUNE — ANDROID PROJECT
===========================

This is the complete Android Studio/Gradle source project for SoundTune.
It targets Android 13+ (minimum Android 6.0). The build workflow creates a
signed-by-debug-key APK suitable for personal testing and sideloading.

FEATURES
--------
- AMOLED dark interface
- 10-band in-app equalizer (60 Hz to 16 kHz, +/-12 dB)
- Balanced, Deep Bass, Vocal, Rock, EDM and Movie presets
- Bass boost, treble and output gain
- Audio file picker and in-app player
- Save and delete multiple custom profiles locally
- Optional Android output-mix equalizer attempt
- No account, ads, analytics or network permission required

IMPORTANT AUDIO LIMITS
----------------------
1. The in-app equalizer processes audio played by SoundTune's own player.
2. The "Try system EQ" button attempts Android's legacy output-mix audio
   effect (audio session 0). Android 13/device firmware may reject or ignore
   global effects. It is not guaranteed to affect Spotify, YouTube, calls,
   games or every Bluetooth route. If blocked, the in-app player EQ remains.
3. Ten UI bands are mapped to the nearest bands exposed by the phone's native
   equalizer; the phone may expose fewer than ten bands and a narrower range.
4. This app cannot change Xiaomi Sound Outdoor's internal DSP, firmware,
   speaker battery readout or proprietary controls without a supported Xiaomi
   protocol. Pair the speaker in Android Bluetooth settings first.
5. This is a personal-use debug build workflow, not a Play Store release.

BUILD THE APK USING ONLY YOUR ANDROID PHONE
-------------------------------------------
1. Download this ZIP and extract it with Files/ZArchiver.
2. Sign in to github.com or create a free account.
3. Create a new repository named SoundTune (Public or Private).
4. Upload the CONTENTS of the extracted SoundTune-Android-Project folder
   (the app folder, .github folder, build.gradle, settings.gradle and
   gradle.properties). Do not upload the outer folder as an extra nesting
   level. Ensure .github/workflows/build-apk.yml is included.
5. Open the repository's Actions tab. If prompted, enable workflows.
6. Select "Build SoundTune APK" and tap "Run workflow".
7. Wait for the workflow to finish successfully. Open that run and download
   the artifact named "SoundTune-APK".
8. Extract the artifact ZIP and tap app-debug.apk. Allow installation from
   that source if Android asks.

If GitHub mobile hides the Actions controls, open github.com in Chrome and
turn on "Desktop site".

PROJECT BUILD COMMAND (computer/Android build environment)
---------------------------------------------------------
gradle assembleDebug
APK output: app/build/outputs/apk/debug/app-debug.apk
