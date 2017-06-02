///*
import sdk.plugin.SdkPlugin
import sdk.plugin.HelloConfig

buildscript {
	repositories {
		mavenLocal()
	}

	dependencies {
		classpath("sdk.plugin:SdkPlugin:+")
	}
}

plugins {
	id("substance-sdk")
}

/*apply {
	plugin<SdkPlugin>()
}*/

sdkHi {
	greeting = "Test"
	from = "Sample"
}
//*/