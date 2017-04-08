import sdk.util.log.*

fun main(args: Array<String>) {
	App()
}

class App {
	init {
		info("Starting Up", tag = "Overriding Broken Reflection System")
		fatal("That's all, folks")	
	}	
}
