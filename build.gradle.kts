import org.gradle.api.tasks.Delete

task<Delete>("clean") {
	delete(buildDir)
	delete(fileTree("out/").matching {
		include("*.jar")
		include("*.klib*")
		include("*.kexe*")
	})
}