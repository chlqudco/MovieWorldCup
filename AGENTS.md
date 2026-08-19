# Repository Guidelines

## Project Structure & Module Organization

- `app/src/main/java/com/chlqudco/movieworldcup/`: application code
  - `data/`: TMDB networking and DataStore persistence
  - `domain/`: tournament models, engine, and taste analysis
  - `ui/`: ViewModel, Compose screens, components, and theme
  - `share/`: result-card image generation and Android sharing
- `app/src/main/res/`: strings, themes, XML configuration, and the approved TMDB logo
- `app/src/test/`: local JUnit tests
- `app/src/androidTest/`: device and emulator tests
- `gradle/libs.versions.toml`: centralized dependency versions

Keep business rules in `domain`, external I/O in `data`, and rendering or interaction state in `ui`.

## Build, Test, and Development Commands

Run commands from the repository root. On Windows PowerShell, use:

- `.\gradlew.bat assembleDebug`: build the debug APK.
- `.\gradlew.bat testDebugUnitTest`: run local JVM unit tests.
- `.\gradlew.bat lintDebug`: run Android Lint checks.
- `.\gradlew.bat connectedDebugAndroidTest`: run instrumentation tests on a connected device or emulator.
- `.\gradlew.bat installDebug`: install the debug build on a connected device.

Use `./gradlew` on macOS or Linux.

## Coding Style & Naming Conventions

Follow Kotlin official style with four-space indentation. Use `PascalCase` for classes, data classes, enums, and composable functions; use `camelCase` for functions and properties. Name test files after the subject, such as `TournamentEngineTest.kt`, and Android resources with `snake_case`.

Prefer immutable state, small composables, `StateFlow` for presentation state, and suspend functions for I/O. Do not add code comments or KDoc unless the user explicitly requests them.

## Testing Guidelines

JUnit 4 is used for local tests. Add deterministic unit tests for tournament progression, undo behavior, persistence transformations, and taste analysis. Place Android-dependent UI or integration tests under `androidTest`. No coverage threshold is enforced; add focused regression tests. Run unit tests and lint before opening a pull request.

## Commit & Pull Request Guidelines

The history currently contains only `Initial commit`, so no established convention exists. Use short, imperative subjects, optionally with a conventional prefix, for example `feat: add decade tournament mode` or `fix: restore final-round state`.

Pull requests should describe user-visible changes, list verification commands, link related issues, and include screenshots for Compose UI changes. Keep changes scoped and never include generated build output or secrets.

## Security & Configuration

Set `TMDB_READ_ACCESS_TOKEN` only in the Git-ignored `local.properties` file. Never commit or paste the token into source, tests, logs, or pull requests. Preserve the TMDB attribution notice and approved logo when changing the About screen.
