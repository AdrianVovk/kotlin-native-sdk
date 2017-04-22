package sdk

object System {

	fun exit(errorCode: Int = 0) = c.stdlib.exit(errorCode)

}
