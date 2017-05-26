package sdk

import kotlinx.cinterop.*

import c.stdlib.*

object System {

	fun exit(errorCode: Int = 0): Unit = c.stdlib.exit(errorCode)

}

object Platform {
	val platform: String
		get() {
			return "Linux"
		}

	fun isLinux() = platform == "linux"
	fun isMacOs() = platform == "darwin"

}
