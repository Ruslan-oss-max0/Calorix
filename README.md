# Calorix — Calorie Counter for Android

A simple, offline calorie tracker for Android. Built with AI coding tools (Google AI Studio, Claude Code / Codex) and reviewed, tested and finished by hand.

**Download:** see the latest APK in [Releases](../../releases) · **Demo video:** _add link here_

## Features
- **Today screen** with a calorie ring showing progress toward your daily goal
- **Add food entry:** dish name, calories and meal type (Breakfast, Lunch, Dinner, Snack)
- **History** of logged entries, with an empty state when nothing is logged yet
- **Settings:** change your daily calorie target and clear all logs (with a confirmation dialog)
- Works fully **offline** — all data is stored on the device

## Tech stack
- Kotlin, Jetpack Compose, Material 3
- MVVM: ViewModel + UI state, Kotlin Coroutines / Flow
- Room database (local food log), DataStore (user preferences)
- Navigation Compose
- Min SDK 24 (Android 7.0), target SDK 35

## Project structure
```
app/src/main/java/com/calorix/app/
├── data/        # Room entity, DAO, database, repositories, MealType
└── ui/
    ├── screens/     # Home, AddFood, History, Settings
    ├── components/  # CalorieRing, FoodEntryItem, EmptyStateView
    ├── navigation/  # NavRoutes, AppNavigation
    ├── viewmodel/   # CalorieViewModel, CalorieUiState
    └── theme/       # Colors, typography, theme
```

## Run it
1. Open the project in Android Studio and press **Run**, or
2. Install `app-release.apk` from Releases on an Android phone (allow installs from unknown sources).

## Author
Ruslan Kryzhanskiy · Telegram [@ruslan_num0](https://t.me/ruslan_num0) · [GitHub](https://github.com/Ruslan-oss-max0)
