// Import all of the Kotlin/Native gradle plugin configurations
import org.jetbrains.kotlin.gradle.plugin.*

// Include Kotlin/Native
buildscript {
    repositories {
        mavenCentral()
        maven { setUrl("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-native-gradle-plugin:+")
    }
}

apply {
	plugin<KonanPlugin>()
}

configure<KonanArtifactsContainer> {
	create("sample", closureOf<KonanCompilerConfig> {
		inputDir("src/")
		outputDir("../out/")

		library("../out/sdk-library.bc") // NOT-WORKING: This doesn't work yet. Will eventually be implimented

		enableOptimization() // Make smaller binaries at expense of compile time
	})
}