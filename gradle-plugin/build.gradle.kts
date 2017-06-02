import org.gradle.api.tasks.Copy
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

buildscript {

    repositories {
        gradleScriptKotlin()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

apply {
	plugin("kotlin")
	plugin("maven-publish")
}

allprojects {
	group = "sdk.plugin"
	version = "0.0.1"
}

task<Copy>("move-output") {
	dependsOn("jar")
	from(tasks.getByName("jar").outputs.files.singleFile)
	into("../out/")
	rename("gradle-plugin-(.*).jar", "gradle-plugin.jar")
}

tasks.getByName("build") {
	dependsOn("move-output")
	finalizedBy("publishToMavenLocal")
}

repositories { gradleScriptKotlin() }
dependencies {
	compile(kotlinModule("stdlib")) // Include Kotlin Standard Library
	compile(gradleApi()) // Include the Gradle API
	compile(gradleScriptKotlinApi())
}

configure<PublishingExtension> {
	publications {
		create<MavenPublication>("mavenJavaLibrary") {
            artifactId = "SdkPlugin"
			artifact("../out/gradle-plugin.jar")
		}
	}
}