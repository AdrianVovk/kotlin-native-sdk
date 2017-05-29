import sdk.plugin.*

buildscript {

	repositories {
		mavenLocal()
	}

	dependencies {
		classpath("sdk.plugin:gradle-plugin:+")
	}
}

apply {
	plugin("substance-sdk")
}