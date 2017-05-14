# Gradle Plugin
This project is a gradle plugin for using the Substance SDK. This will eventually allow for a codebase that works on multiple platforms and targets.

### Potential build file:
```kotlin

import sdk.SubstanceSDK
import sdk.*

// Inclue gradle plugin

configure<SubstanceSDK> {
  appName("MyApp")
  appPkg("com.example.app")
  sourceFolder("src/")
  outputFolder("out/")
  platforms {
    android {
      manifest {
        supportsMultiwindow()
      }
      sourceFolder("src/android")
    }

    linux {
      sourceFolder("src/linux")
      disableOptimization()
    }
  }
  windows {
    window("Main", "windows.MainWindow") {
      metadata(Window.META_LAUNCHER)
      metadata(Window.META_MAIN)
    }
    window("Details", "windows.DetailsWindow")
    window("About", "windows.AboutWindow")
  }
  
  services {
    service("MusicService", "services.MusicPlayer") {
      opens(MimeType("audio/mp3"))
    }
    
    service("VideoService", "services.VideoPlayer") {
      opens(MimeType("video/mp4"))
    }
  }
}

dependencies {
  compileAndroid("androidLib") version "1.0"
  compileLinux("libc") version "+"
}
```
