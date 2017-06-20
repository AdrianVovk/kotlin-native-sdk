plugins {
	id("substance.SdkPlugin") version "0.0.1"
}

sdk {
	suppressPlatformWarning = true

	appId = "com.app"
	appName = "Hello Demo App"

	windows {
		"windows.Home" {
			main()
		}
		"windows.Details"()
	}

	jvm.main = "jvm.HelloKt"

	android {
		compileSdkVersion = 26
		buildToolsVersion = "26.0.0"
	}
}