# Kotlin/Native SDK
This repo shows off Kotlin/Native and the Substance SDK with a simple GTK Program.

## Building
You have two ways to build this:

*__Note:__ The first build will take a few minutes and requires a bit of RAM. Subsequent builds require less ram and are faster*

##### Gradle

This uses the Kotlin/Native Gradle plugin to build the project.

To build, run `scripts/gradlew build`. The built binary will be placed in the `out/` folder.

To clean the working directory, run `scripts/gradlew clean`.

*Depending on your configuration, it might be easier to replace `scripts/gradlew` with `gradle`*

##### Included Build script

To build, run `scripts/build` and follow the prompts. The builr binary will be placed in the	`out/` folder.
Subsequent builds will ask if you want to rebuild the libraries. If you are experiencing issues with compilation, try answering `y` to these prompts.

To clean the working directory, run `scripts/clean`.