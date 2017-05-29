package sdk.plugin

import org.gradle.api.*
import org.gradle.script.lang.kotlin.*

class SdkPlugin() : Plugin<Project> {
	override fun apply(project: Project) {
		println("Configuring SdkPlugin")
		project.createTask("test", DefaultTask::class) {
			println("Hello World")
		}
	}
}