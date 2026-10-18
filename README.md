# fire-tv-simple-launcher
A lightweight Fire TV launcher that strips away Amazon bloat and focuses on simple app launching

## Features

- Grid of installed apps (Leanback/launcher activities) and **website shortcuts** mixed together.
- Website tiles have a blue style and a `LINK` badge; they open in the default browser via `ACTION_VIEW`.
- Select **+ Add website** to add a link; long-press (hold select) a website tile to edit or remove it.
- Shortcuts are stored in `websites.json` in the app's files dir (seeded with YouTube, Netflix, Google).

Build with `gradle :app:assembleDebug` (Android SDK required); unit tests: `gradle :app:testDebugUnitTest`.
