package sdk.args

class Arguments
object ArgProcessor {
	fun from(args: Array<String>): Arguments {
		return Arguments()
	}
}

class ArgConfig(val desc: String) {
	public var data = mutableListOf<Arg>()

	fun add(long: String, short: Char, desc: String, optional: Boolean = true) : ArgConfig {
		data.add(Arg(long, short, desc, optional))
		return this
	}
}

data class Arg(val longName: String,
   val shortName: Char,
   val desc: String,
   val optional: Boolean = true)

/*
sealed class <T> Argument(val shortName: String?,
		val fullName: String, val default: T) {

	// All supported argument types
	data class BooleanArg(s: String?, f: String, ) : Argument(s, f, )
	data class StringArg(s: String?, f: String, ) : Argument(s, f, )
	data class Double(s: String?, f: String, ) : Argument(s, f, )
	data class Int(s: String?, f: String, ) : Argument(s, f, )
	data class Long(s: String?, f: String, ) : Argument(s, f, )

	//
	var data: T = default
}
*/