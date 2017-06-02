package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

object Constants {
	const val HELLO_TASK = "hello"
	const val HELLO_EXT = "sdkHi"
}

open class SdkPlugin() : Plugin {

	override fun apply(project: Project) {
		with(project) {
			extensions.create<HelloConfig>(Constants.HELLO_EXT, HelloConfig::class.java)
			task<HelloTask>(Constants.HELLO_TASK)
		}
	}

}