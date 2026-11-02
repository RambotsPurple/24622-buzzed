# FTC Robot Controller Debugging Incidents

Recorded: 2026-11-02

Project versions involved:

- FTC SDK `12.0.0`
- Sloth `0.3.2`
- Sloth FullPanels `0.3.2+1.0.13`
- SolversLib `0.3.6`
- EJML `0.39` (transitive dependency of SolversLib)

## Summary

| Symptom | Actual cause | Fatal? | Fix |
| --- | --- | --- | --- |
| `NoClassDefFoundError: javax/swing/JPanel` | Reflection scanners reached EJML's optional desktop visualization classes | No; the observed scanner errors were caught | Make Panels ignore `org.ejml` |
| Robot Controller repeatedly crashed after installation | Sloth loaded an old OnBot Java JAR entry using slash-separated class notation | Yes | Archive the old OnBot Java source and build output |

The `JPanel` messages were real, but they distracted from the later `FATAL EXCEPTION` that actually killed the Robot Controller.

## Incident 1: Panels, SolversLib, EJML, and `JPanel`

### Dependency chain

```text
TeamCode
  -> SolversLib
    -> ejml-simple
      -> DMatrixComponent / FMatrixComponent
        -> javax.swing.JPanel
```

Android does not include Java Swing. EJML's matrix operations work on Android, but EJML also ships optional desktop visualization classes that extend `JPanel`.

Panels and Sinister perform reflection-based classpath scanning. Loading the EJML visualization classes makes Android try to resolve their Swing superclass, producing:

```text
NoClassDefFoundError: Failed resolution of: Ljavax/swing/JPanel;
```

SolversLib does not modify Panels, and the PID controller does not use Swing. The problem is caused by a scanner loading an unrelated EJML class.

### Project workaround

`config/util/PanelsCompatibility.java` runs before Panels' event-loop initialization and adds `org.ejml` to Panels' ignored packages:

```java
@OnCreate
public static void initialize(Context context) {
    ClassFinder.INSTANCE
            .getDEFAULT_IGNORED_PACKAGES()
            .add("org.ejml");
}
```

This hook requires one full APK installation because Sloth hot loading occurs after application startup:

```powershell
.\gradlew.bat clean :TeamCode:installDebug
```

Sinister and the FTC SDK may still print caught EJML/JPanel scanner errors. A `JPanel` line by itself is not proof that the process crashed; find the subsequent `FATAL EXCEPTION`.

### Suggested upstream Panels fix

Add `"org.ejml"` to `DEFAULT_IGNORED_PACKAGES` in Panels' `ClassFinder.kt`, or expose a supported ignored-package configuration API.

Panels repository: <https://github.com/ftcontrol/ftcontrol-panels>

## Incident 2: Fatal Sloth/OnBot Java `DriveMain` crash

### Fatal error

The actual crash repeatedly contained:

```text
FATAL EXCEPTION: OpModeThread
java.lang.Error: java.lang.ClassNotFoundException:
Invalid name: org/firstinspires/ftc/teamcode/DriveMain
    at dev.frozenmilk.sinister.SinisterImpl.load(...)
```

The Control Hub still had an old OnBot Java project and compiled output from 2025:

```text
/sdcard/FIRST/java/src/org/firstinspires/ftc/teamcode/DriveMain.java
/sdcard/FIRST/java/build/jars/.../OnBotJava.jar
```

The JAR contained the normal ZIP entry:

```text
org/firstinspires/ftc/teamcode/DriveMain.class
```

Sloth `0.3.2` removes the `.class` suffix but does not convert `/` to `.` before calling `Class.forName()`. Java requires this binary name:

```text
org.firstinspires.ftc.teamcode.DriveMain
```

The slash-separated name is invalid and Sloth rethrows the exception, causing the crash loop.

Current upstream source showing the missing conversion:
<https://github.com/Dairy-Foundation/Sloth/blob/master/src/main/kotlin/dev/frozenmilk/sinister/OnBotJavaLoader.kt>

### Why Git reverts and APK reinstalls did not help

OnBot Java data is stored in shared device storage under `/sdcard/FIRST/java`. Reverting the repository, cleaning Gradle, uninstalling, or reinstalling TeamCode does not necessarily remove that directory.

### Fix performed

The Robot Controller was stopped, and the active OnBot Java source and compiled build were moved, not deleted, to:

```text
/sdcard/FIRST/java/backup-onbot-20261102/src
/sdcard/FIRST/java/backup-onbot-20261102/build
```

After restarting the Robot Controller:

- The process remained alive past the previous crash point.
- No new `DriveMain` fatal exception occurred.
- Robot status reached `running`.
- Panels servers started.
- `ExternalLibrariesLoader` and `OnBotJavaLoader` completed processing.

### Inspecting the backup

```powershell
adb shell find /sdcard/FIRST/java/backup-onbot-20261102 -type f
```

Do not restore this backup into the active `src` and `build` locations while using the affected Sloth loader; doing so will reintroduce the crash. Copy it to a computer first if any old source is needed:

```powershell
adb pull /sdcard/FIRST/java/backup-onbot-20261102 .\onbot-backup
```

### Suggested upstream Sloth fix

Normalize every JAR class entry before passing it to `Class.forName()`:

```kotlin
.map {
    it.substring(0, it.length - ".class".length)
        .replace('/', '.')
}
```

## Useful Commands

Run these from the repository root unless noted otherwise.

### Verify ADB connection

```powershell
adb devices -l
```

### Full Robot Controller installation

`TeamCode` is the Android application module. `FtcRobotController` is a library module.

```powershell
.\gradlew.bat clean :TeamCode:installDebug
```

For normal subsequent installs, `clean` is optional:

```powershell
.\gradlew.bat :TeamCode:installDebug
```

### Download the current persistent Robot Controller log

```powershell
adb pull /sdcard/robotControllerLog.txt .\rc-log.txt
```

The repository's `rc-log.txt` is only a downloaded snapshot and does not update automatically.

### Search for the fatal cause

```powershell
rg -n "FATAL EXCEPTION|Caused by:|NoClassDefFoundError|ClassNotFoundException" .\rc-log.txt
```

Prioritize the exception beneath `FATAL EXCEPTION`. Scanner messages labeled as errors can still be caught and nonfatal.

