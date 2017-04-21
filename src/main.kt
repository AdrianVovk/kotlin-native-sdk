import sdk.util.log.*
import gtk.*
import kotlinx.cinterop.*

fun main(args: Array<String>) {
	App(args)
}

// Note that all callback parameters must be primitive types or nullable C pointers.
fun <F : CFunction<*>> g_signal_connect(obj: CPointer<*>, actionName: String,
        action: CPointer<F>, data: gpointer? = null, connect_flags: Int = 0) {
    g_signal_connect_data(obj.reinterpret(), actionName, action.reinterpret(),
            data = data, destroy_data = null, connect_flags = connect_flags)
}

class App(args: Array<String>) : CustomTagLoggable {

	override var tag = "App" as String?

	init {
		info("Starting Up")

		val app = gtk_application_new("org.gtk.example", G_APPLICATION_FLAGS_NONE)!!
	    g_signal_connect(app, "activate",
	    	staticCFunction { app: CPointer<GtkApplication>?, user_data: gpointer? ->
				// ----- START SETUP ----- //

				sDebug("Creating Window")
	    		val windowWidget = gtk_application_window_new(app)!!
   			    val window = windowWidget.reinterpret<GtkWindow>()


   			    gtk_window_set_title(window, "GTK+");
   			    gtk_window_set_default_size(window, 200, 200)

				sDebug("Creating Button Box")
   			    val button_box = gtk_button_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL)!!
   			    gtk_container_add(window.reinterpret(), button_box);

				sDebug("Creating Button")
   			    val button = gtk_button_new_with_label("Click here to print out output")!!
   			    g_signal_connect(button, "clicked", staticCFunction { widget: CPointer<GtkApplication>?, data: gpointer? ->
   							sInfo("Button Clicked", tag = "Button")
	            });
   			    gtk_container_add(button_box.reinterpret(), button);

				sDebug("Showing Window")
   			    gtk_widget_show_all(windowWidget)

   			    // ----- END SETUP ----- //
	    	})
	    val status = memScoped {
	        g_application_run(app.reinterpret(),
	                args.size, args.map { it.cstr.getPointer(memScope) }.toCValues())
	    }
	    g_object_unref(app)

	    info("Exitting")
	}

}

fun sDebug(text: String) = sDebug(text, "Setup")

fun sDebug(text: String, tag: String) = Any().debug(text, tag = tag)

fun sInfo(text: String) = sInfo(text, "Setup")

fun sInfo(text: String, tag: String) = Any().debug(text, tag = tag)