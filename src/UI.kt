package sdk

import sdk.util.log.*
import sdk.System
import gtk.*
import kotlinx.cinterop.*

// Note that all callback parameters must be primitive types or nullable C pointers.
fun <F : CFunction<*>> g_signal_connect(obj: CPointer<*>, actionName: String,
        action: CPointer<F>, data: gpointer? = null, connect_flags: Int = 0) {
    g_signal_connect_data(obj.reinterpret(), actionName, action.reinterpret(),
            data = data, destroy_data = null, connect_flags = connect_flags)
}

abstract class Application(args: Array<String>, val id: String, val flags : GApplicationFlags = G_APPLICATION_FLAGS_NONE) {
	init {
		info("Starting $id", tag = "SDK Application")
		val app = gtk_application_new(id, flags)!!
		g_signal_connect(app, "activate", setup())

		verbose("Configured $id; Running", tag = "SDK Application")
		val status = memScoped {
			g_application_run(app.reinterpret(), args.size, args.map { it.cstr.getPointer(memScope) }.toCValues())
		}

		info("Cleaning Up", tag = "SDK Application")
		g_object_unref(app)
		cleanup()r

		info("Quitting with $status", tag = "SDK Application")
		System.exit(status)
	}

	abstract fun setup() : CPointer<CFunction<(app: CPointer<GtkApplication>?, user_data: gpointer?) -> Unit>>

	fun cleanup() {
		// Does nothing by default; for developer to implement if needed
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

	val widget = gtk_button_new()!!
	val internal = widget.reinterpret<GtkButton>()

	inline var text : String
		set(value) = gtk_button_set_label(internal, value)
		get() = gtk_button_get_label(internal)?.toKString() ?: "ERROR"

	inline fun onClick(callback: GCallback) : Unit = g_signal_connect(internal, "clicked", callback)

	inline fun addTo(container: CPointer<GtkWidget>?) = gtk_container_add(container!!.reinterpret(), widget)

	init {
		if (initText != null) text = initText
		toRun()
	}
}

