package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.gradle.api.plugins.JavaPluginConvention

import org.gradle.api.tasks.JavaExec
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.diagnostics.TaskReportTask

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

////////////////////////////////////////////////////////
// Sandboxing
////////////////////////////////////////////////////////

fun Project.sandbox(name: String): Project? {
	// Creates a sandbox for applying plugins
	try {
		val proj = project(":$name")
		proj.buildDir = file("$rootDir/build/")
		proj.extensions.add(Constants.SDK_EXT, meta) // Give it the metadata object

		val projectFolder = file("$rootDir/$name/")
		if (!projectFolder.isDirectory()) {
			if (projectFolder.exists()) { // If it exists but isn't a directory
                throw GradleException("Please rename $rootDir/$name, as it is causing problems with the build")
			}
			mkdir(projectFolder.absolutePath)
			gradle.buildFinished {
				projectFolder.delete()
			}
		}

		proj.afterEvaluate {
			for (task in tasks.withType<JavaExec>()) {
				task.workingDir = rootDir // Fix Java workingDir
			}
		}
		return proj
	} catch (e: UnknownProjectException) {
		if (!meta.suppressPlatformWarning) println("[WARNING] Skipping support for $name. To use, add `include(\"$name\")` to settings.gradle")
		return null
	}
}

fun Project.modTasksReport() {
	// Override `tasks` task so it doesn't include subprojects
	val tasksTask = getTask("tasks") as TaskReportTask
	tasksTask.setProjects(setOf(TasksReportProject(this))) // Tell it to use our special project (which just wraps the real one) instead of the normal one
	//tasksTask.description = tasksTask.description.removeSuffix(" (some of the displayed tasks may belong to subprojects).") + "." // Fix the description
}

class TasksReportProject(val proj: Project) : ProjectInternal by (proj as ProjectInternal) {
	override fun getSubprojects(): Set<Project> {
		return proj.subprojects.filter { !arrayOf("jvm", "native", "android").contains(it.name) }.toSet() // Filter out our subprojects
	}
}