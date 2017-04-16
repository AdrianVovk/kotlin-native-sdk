import sdk.util.log.*

//import gtk.*
//import gtk.GtkWindowType
//import gtk.gtk_window_new
//import gtk.gtk_window_set_title
//import gtk.gtk_widget_show_all
//import gtk.G_APPLICATION_FLAGS_NONE
//import gtk.gtk_application_new

fun main(args: Array<String>) {
	App()
}

class App {
	init {
		info("Starting Up", tag = "Overriding Broken Reflection System")
		debug("Testing Debug")
		
		
		val application = gtk.gtk_application_new("demo.app.Test", 0)

		//val window = gtk_window_new(GtkWindowType.GTK_WINDOW_TOPLEVEL)
		//gtk_window_set_title(window, "AYY")
		//gtk_widget_show_all(window)
		
		//fatal("Crashing Now", code = 9)
	}	
}
