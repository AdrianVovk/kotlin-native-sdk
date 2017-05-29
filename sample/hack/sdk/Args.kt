package sdk.args

import sdk.ui.Application

class ArgumentException(message: String): RuntimeException(message)
typealias CmdLine = Array<String>

fun CmdLine.includes(long: String, short: Char) = contains("--$long") or contains("-$short")
fun CmdLine.data(long: String, short: Char): String {
	val pos: Int = when {
		contains("--$long") -> indexOf("--$long")
		contains("-$short") -> indexOf("-$short")
		else -> -1
	}
	if (pos == -1) throw ArgumentException("No data found")
	return get(pos + 1)
}

/*data*/ class Arg<T: Any>(val longName: String,
 	val shortName: Char,
 	val desc: String,
 	val optional: Boolean = true,
	val default: T,
	val computeFun: Arg<T>.(CmdLine) -> T) {
		fun compute(input: CmdLine) = computeFun(input) // HACK
	}
typealias AnyArg = Arg<*>
val processBoolean: Arg<Boolean>.(CmdLine) -> Boolean = {
		when {
			it.includes(longName, shortName) -> true
			else -> default
		}
	}
val processString: Arg<String>.(CmdLine) -> String = {
			when {
				it.includes(longName, shortName) -> it.data(longName, shortName)
				else -> default
			}
		}
val processInt: Arg<Int>.(CmdLine) -> Int = {
		when {
			it.includes(longName, shortName) -> it.data(longName, shortName).toInt()
			else -> default
		}
	}
val processDouble: Arg<Double>.(CmdLine) -> Double = {
		when {
			it.includes(longName, shortName) -> it.data(longName, shortName).toDouble()
			else -> default
		}
	}

fun Application.arguments(cmdLine: CmdLine, make: ArgConfig.() -> Unit) {
	val config = ArgConfig()
	config.make()

	this.supportedArgs = config.output // Tells application what arguments it supports

	// Parse arguments
	for (arg in config.output) {
		this.parsedArguments.put(arg.longName, arg.compute(cmdLine))
	}
}

class ArgConfig() {
	public var output = mutableListOf<AnyArg>()

	fun boolean(longName: String, shortName: Char, desc: String, optional: Boolean = true) =
		output.add(Arg<Boolean>(longName, shortName, desc, optional, false /* default */, processBoolean))

	fun string(longName: String, shortName: Char, desc: String, optional: Boolean = true, default: String) =
		output.add(Arg<String>(longName, shortName, desc, optional, default, processString))

	fun int(longName: String, shortName: Char, desc: String, optional: Boolean = true, default: Int) =
		output.add(Arg<Int>(longName, shortName, desc, optional, default, processInt))

	fun double(longName: String, shortName: Char, desc: String, optional: Boolean = true, default: Double) =
		output.add(Arg<Double>(longName, shortName, desc, optional, default, processDouble))
}

typealias ParsedArguments = MutableMap<String, Any>

class Arguments(private val input: ParsedArguments) {
	companion object { fun staticC(input: ParsedArguments): Arguments = Arguments(input) }

	fun <T> getVal(tag: String) : T = input[tag] as T
	fun getBoolean(tag: String) = getVal<Boolean>(tag)
	fun getString(tag: String) = getVal<String>(tag)
	fun getInt(tag: String) = getVal<Int>(tag)
	fun getDouble(tag: String) = getVal<Double>(tag)
}
