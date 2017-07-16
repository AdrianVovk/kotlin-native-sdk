[ If this is a feature request, remove this template and explain the feature you want to be added ]::
#### Affected subprojects
gradle-plugin, kide-plugin

#### Expected Behavior
kide-plugin interacts with gradle-plugin to build the project

#### Actual Behavior
kide-plugin attempts to interact with gradle-plugin, but it attempts to reference the platform `androd`.
Such a platform doesn't exist

###### Log output: 
```
FAILURE: Build failed with an exception.

* What went wrong:
Project 'androd' not found in root project 'simple-sample'.
```

#### Steps to reproduce
- Install KIDE and gradle
- Clone this repo
- Open simple-sample/ in KIDE
- Press the run button and select Android

#### Details
OS: 

Gradle Version (obtain with `gradle --version`): 4.0.1

Using Gradle wrapper: No

Enabled platforms: native, jvm, android

Gradle Plugin Version (if applicable): 

KIDE Plugin Version (if applicable): 

SDK Library Version (if applicable): 

Commit (if applicable, obtain with `git rev-parse --short HEAD` or commit causing issue): 
