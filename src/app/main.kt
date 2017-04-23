import sdk.util.log.*
import gtk.*
import sdk.*
import kotlinx.cinterop.*

fun main(args: Array<String>) {
	GtkDemoApp(args) // Start Program
}

class GtkDemoApp(args: Array<String>) : Application(args, "org.gtk.demo") {
	override fun setup(app: CPointer<GtkApplication>?) {
 		Window(app) {
			title = "GTK+ Demo"

			val button_box = gtk_button_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL)!!
			addWidget(button_box)

			sDebug("Creating Buttons")
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
	    //gtk_window_set_default_size(window, 200, 200)
	}

}

////////////////////////////////////////////////////////
// HACK: Bypass static behaviour with staticCFunction //
////////////////////////////////////////////////////////

fun sDebug(text: String) = sDebug(text, "Setup")

fun sDebug(text: String, tag: String) = Any().debug(text, tag = tag)

fun sInfo(text: String) = sInfo(text, "Setup")

fun sInfo(text: String, tag: String) = Any().debug(text, tag = tag)
