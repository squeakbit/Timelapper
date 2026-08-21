# Build-Fix

The previous project mixed Kotlin/KSP/Compose compiler generations.
This revision aligns them:

- Kotlin 2.2.10
- Compose Compiler Gradle plugin 2.2.10
- KSP 2.2.10-2.0.2
- AGP 8.13.2
- JDK/JVM target 17
- Compose BOM 2026.06.00

If `compileDebugKotlin` still reports `BackendException`, run:

./gradlew :app:compileDebugKotlin --stacktrace

and provide the first `Caused by:` block. At that point the remaining error is likely in a specific compiler plugin/source file rather than the Kotlin/Compose/KSP version alignment.
