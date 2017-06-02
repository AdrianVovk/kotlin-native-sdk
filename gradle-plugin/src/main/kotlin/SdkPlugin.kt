package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*

import org.gradle.script.lang.kotlin.*

open class SdkPlugin() : Plugin<Project> {
	override fun apply(project: Project) {

		project.extensions.create<HelloConfig>("sdkHi", HelloConfig::class.java)
		project.task<HelloTask>("hello")

		project.task<DefaultTask>("goodbye") {
			doLast {
				println("Goodbye!")
			}
		}

	}
}

fun Project.sdkHi(config: HelloConfig.() -> Unit) = configure<HelloConfig>(config)