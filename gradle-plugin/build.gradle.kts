import org.gradle.api.tasks.Copy
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import groovy.util.*

plugins {
	id("nebula.kotlin") version "1.1.2"
	id("maven-publish")
	id("java-gradle-plugin")
	id("org.jetbrains.dokka") version "0.9.14-eap-2"
}

group = "sdk.plugin"
version = "0.0.0"

repositories {
	gradleScriptKotlin()
	maven { setUrl("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
}
dependencies {
	compile(kotlinModule("stdlib")) // Include Kotlin Standard Library
	compile(gradleApi()) // Include the Gradle API
	compile(gradleScriptKotlinApi()) // Include the Gradle-Script-Kotlin API

	compile("org.jetbrains.kotlin:kotlin-native-gradle-plugin:0.2")
}

task<Copy>("move-output") {
	dependsOn("jar")
	from(tasks.getByName("jar").outputs.files.singleFile) // Take the output of the jar file
	into("../out/") // And put it in the out folder
	rename("gradle-plugin-(.*).jar", "gradle-plugin.jar") // Rename the output jar file
}
tasks.getByName("build").dependsOn("move-output")

/////////////////////////////////////////////////////////////////////
// Gradle Plugin and Publishing
/////////////////////////////////////////////////////////////////////

publishing {
	repositories {
	    maven { setUrl("../out/maven-repo") }
	}
}

gradlePlugin {
  	plugins {
    	create("SdkPlugin") {
      		id = "substance.SdkPlugin"
      		implementationClass = "sdk.plugin.SdkPlugin"
      		//version = "SNAPSHOT" // TODO
    	}
	}
}