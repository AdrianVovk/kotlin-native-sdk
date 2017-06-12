package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.jetbrains.kotlin.gradle.plugin.*

fun Project.configureKonan() {
	pluginManager.apply(KonanPlugin::class.java)

	val normAppName = meta.appName.split(" ", "_").map { it.capitalize()  }.joinToString("").capitalize()

	//////////////////////////////
	// Configure konan compiler //
	//////////////////////////////

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
		normAppName {
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

	///////////
	// Tasks //
	///////////

	val oldBuild = getTask("build")
	tasks.remove(oldBuild)
	val build = getTask(Constants.KONAN_COMPILE_TASK)
	build.setDependsOn(oldBuild.dependsOn) // Transfer build dependencies

	build.dependsOn(Constants.METADATA_TASK) // Add metadata task to the build process
	getTask("compileKonan$normAppName").mustRunAfter(Constants.METADATA_TASK) // Generate metadata before build

	task<GenDefsTask>(Constants.NATIVE_DEF_TASK)
	build.dependsOn(Constants.NATIVE_DEF_TASK) // Add def task to the build process
	for (interop in meta.native.interops) {
		getTask("gen${interop.name.capitalize()}InteropStubs").mustRunAfter(Constants.NATIVE_DEF_TASK) // Generate def file before building
	}

	task<Exec>(Constants.KONAN_RUN_TASK) {
		dependsOn(build)

		val fileExt = if (System.getProperty("os.name").startsWith("Windows")) "exe" else "kexe"
		val out = meta.outputDir.removeSuffix("/")
		val args = (properties[Constants.KONAN_RUN_ARGUMENTS] as String?)?.split(" ")?.toTypedArray<String>() ?: arrayOf<String>()
		commandLine("$out/$normAppName.$fileExt", *args)
	}
}

open class GenDefsTask : DefaultTask() {
	@TaskAction fun generate() {
		with(project) { file("$buildDir/sdk/nativeDefs/") }.mkdirs() // Make the directory
		for (def in meta.native.defFilesToGenerate) {
			val outputFile = with(project) { file("$buildDir/sdk/nativeDefs/${def.name}.def") }
			var text = ""

			if (def.headers != "NONE") text += "headers = ${def.headers}\n"
			if (def.compilerOpts != "NONE") text += "compilerOpts = ${def.compilerOpts}\n"
			if (def.headerFilter != "NONE") text += "headerFilter = ${def.headerFilter}\n"
			if (def.excludeDependentModules != null) text += "excludeDependentModules = ${def.excludeDependentModules}\n"
			if (def.includeC != "NONE") {
				val c = """
					---
					${def.includeC}
				""".trimIndent().trim() // Fixes spacing
				text += c
			}

			outputFile.writeText(text)
		}
	}
}