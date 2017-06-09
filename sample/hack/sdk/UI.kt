package sdk.ui

import sdk.util.log.*
import sdk.args.*
import sdk.System
import gtk.*
import kotlinx.cinterop.*
import sdk.gen.BuildMetadata

// Note that all callback parameters must be primitive types or nullable C pointers.
fun <F : CFunction<*>> g_signal_connect(obj: CPointer<*>, actionName: String,
        action: CPointer<F>, data: gpointer? = null, connect_flags: Int = 0) {
    g_signal_connect_data(obj.reinterpret(), actionName, action.reinterpret(),
            data = data, destroy_data = null, connect_flags = connect_flags)
}

fun <T> obtainInstance(pointer: COpaquePointer? /* CPointer<out CPointed>? */) = StableObjPtr.fromValue(pointer!!).get() as T

abstract class Application(val args: Array<String>, val execName: String = BuildMetadata.APP_NAME, val id: String = BuildMetadata.APP_ID, val flags : GApplicationFlags = G_APPLICATION_FLAGS_NONE) {
	val ptr = StableObjPtr.create(this)

  abstract fun setup(app: CPointer<GtkApplication>?, args: Arguments) : Unit

  open var supportedArgs: MutableList<AnyArg> = mutableListOf()

  open var parsedArguments: ParsedArguments = mutableMapOf()

  open fun cleanup() {
    // Does nothing by default; for developer to implement if needed
  }

	init {
		info("Starting $execName as $id", tag = "SDK Application")
		val app = gtk_application_new(id, flags)!!

		// Process arguments
		val gtkargs = arrayOf(execName).union(args.asIterable()) // Needed to include basename for GTK to work
		for (arg in supportedArgs) {
			//TODO: Add support for passing data
			g_application_add_main_option(app.reinterpret(),
			   arg.longName,
			   arg.shortName.toByte(),
			   if (arg.optional) G_OPTION_FLAG_OPTIONAL_ARG else G_OPTION_FLAG_NONE,
			   GOptionArg.G_OPTION_ARG_NONE,
			   arg.desc,
			   null)
		}


		// Creating program initiaton
		g_signal_connect(app, "activate",
					staticCFunction { app: CPointer<GtkApplication>?, user_data: gpointer? ->
						val instance = obtainInstance<Application>(user_data) // This makes a pointer to call the setup function
						instance.setup(app, Arguments.staticC(instance.parsedArguments))
					}, data = ptr.value)

		// Run program
		verbose("Configured $execName; Running", tag = "SDK Application")
		val status = memScoped {
			g_application_run(app.reinterpret(), gtkargs.size, gtkargs.map { it.cstr.getPointer(memScope) }.toCValues())
		}

		// Cleanup
		info("Cleaning Up $execName", tag = "SDK Application")
		g_object_unref(app)
		cleanup()
		ptr.dispose()

		info("Quitting $execName with $status", tag = "SDK Application")
		System.exit(status)
	}

}

class Window(val app: CValuesRef<GtkApplication>?, initTitle: String? = null, toRun: Window.() -> Unit = {}) : CustomTagLoggable {

	override var tag: String? = "Gtk Window"

	//TODO: Allow for non application windows

	val widget = gtk_application_window_new(app)!!
	val internal = widget.reinterpret<GtkWindow>()

	inline var title : String
		set(value) = gtk_window_set_title(internal, value)
		get() = gtk_window_get_title(internal)?.toKString() ?: "ERROR"

	fun populate(makeWidget: () -> CPointer<GtkWidget>?) = addWidget(makeWidget())

	fun addWidget(widget: CPointer<GtkWidget>?) = gtk_container_add(internal!!.reinterpret(), widget)

	fun show() = gtk_widget_show_all(widget)

	init {
		if (initTitle != null) title = initTitle
		toRun()
	}
}

class Button(initText: String? = null, toRun: Button.() -> Unit = {}) {

	val ptr = StableObjPtr.create(this)

	val widget = gtk_button_new()!!
	val internal = widget.reinterpret<GtkButton>()

	inline var text : String
		set(value) = gtk_button_set_label(internal, value)
		get() = gtk_button_get_label(internal)?.toKString() ?: "ERROR"

	var onClick: (widget: CPointer<GtkWidget>?) -> Unit = {}
		set(value) {
			field = value
			g_signal_connect(internal, "clicked", staticCFunction { widget: CPointer<GtkWidget>?, data: gpointer? ->
				val inst = obtainInstance<Button>(data)
				inst.onClick(widget)
			}, ptr.value)
		}

	inline fun addTo(container: CPointer<GtkWidget>?) = gtk_container_add(container!!.reinterpret(), widget)

	init {
		if (initText != null) text = initText
		toRun()
	}
}
