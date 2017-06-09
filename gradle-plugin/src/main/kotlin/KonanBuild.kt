package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.jetbrains.kotlin.gradle.plugin.*

fun Project.configureKonan() {
	pluginManager.apply(KonanPlugin::class.java)

	// Configure konan compiler

	val konanArtifacts: NamedDomainObjectContainer<KonanCompilerConfig> by project.extensions
	konanArtifacts {
		"Application" {
			inputDir("src/native/")
			inputDir("src/shared")
			inputFiles("build/sdk/metadata.kt") // Include generated metadata

			outputDir(meta.outputDir)

			if (meta.nativeOptimize) enableOptimization()
		}
	}

	//TODO: Configure interop

	// The konan plugin configures the build task with everything needed to build
	// This just renames the whole konan build process to something else.
	val oldBuild = getTask("build")
	tasks.remove(oldBuild)
	val build = getTask(Constants.KONAN_TASK)
	build.setDependsOn(oldBuild.dependsOn)
	build.dependsOn(Constants.METADATA_TASK) // Add metadata task to the build process
	getTask("compileKonanApplication").mustRunAfter(Constants.METADATA_TASK)
	getTask(Constants.ALL_TASK).dependsOn(build)
}