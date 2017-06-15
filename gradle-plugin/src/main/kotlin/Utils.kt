package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.gradle.api.plugins.JavaPluginConvention

import org.gradle.api.initialization.Settings

internal fun Project.getTask(name: String) : Task {
	val tasks = project.getTasksByName(name, false)
	return if (tasks.isEmpty()) {
		task<DefaultTask>(name)
	} else {
		tasks.single()
	}
}

internal fun <T> Project.ext(name: String): T = extensions.findByName(name) as T
internal val Project.meta	get() = ext<SdkConfig>(Constants.SDK_EXT)
internal val Task.meta		get() = project.meta

val SourceSet.kotlin		get() = ((this as HasConvention).convention.plugins["kotlin"] as KotlinSourceSet).kotlin
val Project.java 			get() = convention.getPlugin(JavaPluginConvention::class.java)

fun String.fullName(meta: SdkConfig) = when {
		startsWith("${meta.appId}.") -> this
		startsWith(".") -> "${meta.appId}$this"
		else -> "${meta.appId}.$this"
}

fun String.fromParent(proj: Project) = proj.parent.getTask(this)

fun Project.sandbox(name: String): Project? {
	// Creates a sandbox for applying plugins
	try {
		val proj = project(":$name")
		proj.buildDir = file("build/")
		proj.extensions.add(Constants.SDK_EXT, meta) // Give it the metadata object
		return proj
	} catch (e: Exception) {
		println("Skipping support for target: $name. To use, add `include(\"$name\")` to settings.gradle")
		return null
	}
}