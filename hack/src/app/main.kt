import sdk.util.log.*
import gtk.*
import sdk.ui.*
import sdk.args.*
import kotlinx.cinterop.*

fun main(cmdline: Array<String>) {
	GtkDemoApp("gtkdemo", cmdline) // Start Program
}

class GtkDemoApp(execName: String, cmdline: Array<String>) : Application(execName, cmdline, "org.gtk.demo") {

	override fun supportedArgs() = ArgConfig("A simple demo app showing off Kotlin/Native with a simple Substance SDK app using GTK")
		.add("version", 'v', "Print out version of the prgram")
		.add("output", 'o', "Output logs")

	override fun setup(app: CPointer<GtkApplication>?, args: Arguments) {
 		Window(app, "GTK+ Demo") {
			title = "GTK+ Demo"

			val button_box = gtk_button_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL)!!
			addWidget(button_box)

			debug("Creating Buttons")
			Button("Click here for some output") {
	          	onClick = {
	          		info("Clicked", tag = "Button 1")
	          	}
				addTo(button_box)
			}

			Button() {
				text = "Click here for some other output"
				onClick = {
					info("Clicked", tag = "Button 2")
				}
				addTo(button_box)
			}
		}.show()


		val appMenu : CPointer<GMenu> = g_menu_new()!!
		g_menu_append(appMenu, "Test", "app.quit")
		gtk_application_set_app_menu(app, appMenu.reinterpret<GMenuModel>())
		g_object_unref(appMenu)

	    //gtk_window_set_default_size(window, 200, 200)
	}

}
