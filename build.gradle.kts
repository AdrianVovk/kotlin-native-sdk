// Include Kotlin/Native
buildscript {
    repositories {
        mavenCentral()
        maven { it.setUrl("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-native-gradle-plugin:+")
    }
}
