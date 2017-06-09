import sdk.util.log.*
import gtk.*
import sdk.ui.*
import sdk.args.*
import sdk.System
import sdk.Platform
import kotlinx.cinterop.*

fun main(cmdline: Array<String>) { GtkDemoApp(cmdline) }

class GtkDemoApp(cmdline: Array<String>) : Application(cmdline) {

	init { // Configure the application

		// Usage
		//description("A simple demo app showing off Kotlin/Native with a simple Substance SDK app using GTK")
		arguments(cmdline) {
			boolean("version", 'v', "Print out the version of this program")
			boolean("log", 'l', "Print out extra log data with this program")
		}

		if (!Platform.isLinux()) {
			fatal("This program only works on Linux currently")
		}

	}

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
