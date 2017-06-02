package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*

import org.gradle.script.lang.kotlin.*

open public class HelloConfig() {
	var greet: String = "Hello"
	var from: String = "HelloTask"
}

open class HelloTask : DefaultTask() {

	val ext = getProject().getExtensions().findByName("sdkHi") as HelloConfig

	val greet: String	get() = ext.greet
	val from: String	get() = ext.from

	@TaskAction fun goodbye() {
		println("$greet from $from")
	}
}