package sdk

import kotlinx.cinterop.*

import c.stdlib.*

object System {

	fun exit(errorCode: Int = 0): Unit = exit(errorCode)

}
