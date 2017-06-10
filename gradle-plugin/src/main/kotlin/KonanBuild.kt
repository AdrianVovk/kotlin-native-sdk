package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.jetbrains.kotlin.gradle.plugin.*

fun Project.configureKonan() {
	pluginManager.apply(KonanPlugin::class.java)

	// Configure konan compiler
	val konanInterop: NamedDomainObjectContainer<KonanInteropConfig> by project.extensions
	konanInterop {
		for (interop in meta.native.interops) {
			interop.name {
				defFile(interop.defFile)
				if (interop.pkg != "NONE") pkg(interop.pkg)
			}
		}
	}

	val konanArtifacts: NamedDomainObjectContainer<KonanCompilerConfig> by project.extensions
	konanArtifacts {
		"Application" {
			inputDir("src/native/")
			inputDir("src/shared")
			if (meta.inputDir != "NONE") inputDir(meta.inputDir)
			inputFiles("build/sdk/metadata.kt") // Include generated metadata

			outputDir(meta.outputDir)

			for (interop in meta.native.interops.map { it.name }) {
				useInterop(interop)
			}

			if (meta.native.linkerOpts != "NONE") linkerOpts(meta.native.linkerOpts)

			if (meta.native.optimize) enableOptimization()
		}
	}

	//TODO: Configure interop

	// The konan plugin configures the build task with everything needed to build
	// This just renames the whole konan build process to something else.
	val oldBuild = getTask("build")
	tasks.remove(oldBuild)
	val build = getTask(Constants.KONAN_COMPILE_TASK)
	build.setDependsOn(oldBuild.dependsOn) // Transfer build dependencies
	build.dependsOn(Constants.METADATA_TASK) // Add metadata task to the build process
	getTask("compileKonanApplication").mustRunAfter(Constants.METADATA_TASK) // Generate metadata before build

	// Run task
	task<Exec>(Constants.KONAN_RUN_TASK) {
		dependsOn(build)
		commandLine("${meta.outputDir.removeSuffix("/")}/Application.kexe") // TODO: Name detection
	}
}