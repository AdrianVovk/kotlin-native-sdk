package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*

import org.gradle.script.lang.kotlin.*

open public class HelloConfig() {
	var greeting: String = "Hello World"
	var from: String = "SdkPlugin"
}

open class HelloTask : DefaultTask() {

	val ext = getProject().getExtensions().findByName(Constants.HELLO_EXT) as HelloConfig

	val greeting	get() = ext.greeting
	val from		get() = ext.from

	@TaskAction fun hello() {
		println("$greeting from $from")
	}
}