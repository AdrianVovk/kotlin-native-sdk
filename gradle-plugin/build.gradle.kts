import org.gradle.api.tasks.Copy

buildscript {

    repositories {
        gradleScriptKotlin()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

apply {	plugin("kotlin") }

allprojects {
	group = "sdk.gradle.plugin"
	version = "0.0.1"
}

task<Copy>("move-output") {
	dependsOn("jar")
	from(tasks.getByName("jar").outputs.files)
	into("../out/")
}

tasks.getByName("build") {
	it.dependsOn("move-output")
}

repositories { gradleScriptKotlin() }
dependencies {
	compile(kotlinModule("stdlib")) // Include Kotlin Standard Library
	compile(gradleApi()) // Include the Gradle API
}