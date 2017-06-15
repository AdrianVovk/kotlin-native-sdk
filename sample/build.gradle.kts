plugins {
	id("substance.SdkPlugin") version "0.0.0"
}

defaultTasks("update-sources", "native:build")

sdk {
	appName = "SdkDemo"
	appId = "subsance.sdk.Demo"

	debug = false
	suppressPlatformWarning = true

	inputDir = "hack/"
	outputDir = "../out/"

	native {
		interop("gtk", pkg = "gtk") {
			headers = "gtk/gtk.h"
			//headerFilter = "gtk/**"
			compilerOpts = "-I. -pthread -I/usr/include/gtk-3.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/at-spi-2.0 -I/usr/include/dbus-1.0 -I/usr/lib/x86_64-linux-gnu/dbus-1.0/include -I/usr/include/gtk-3.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/mirclient -I/usr/include/mircore -I/usr/include/mircookie -I/usr/include/cairo -I/usr/include/pango-1.0 -I/usr/include/harfbuzz -I/usr/include/pango-1.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/pixman-1 -I/usr/include/freetype2 -I/usr/include/libpng16 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/libpng16 -I/usr/include/glib-2.0 -I/usr/lib/x86_64-linux-gnu/glib-2.0/include"
			//compilerOpts("-pthread", "-I/usr/include/gtk-3.0", "-I/usr/lib64/gtk-3.0/include",
			//	"-I/usr/include/atk-1.0", "-I/usr/include/cairo", "-I/usr/include/pango-1.0",
			//	"-I/usr/include/glib-2.0", "-I/usr/lib64/glib-2.0/include", "-I/usr/include/pixman-1",
			//	"-I/usr/include/freetype2", "-I/usr/include/libpng12")
		}
		interop("time", pkg = "c.time") {
			headers = "time.h"
		}
		interop("stdlib", pkg = "c.stdlib") {
			headers = "stdlib.h"
		}
		linkerOpts = "-L/usr/lib/x86_64-linux-gnu -lglib-2.0 -lgdk-3 -lgtk-3 -lgio-2.0 -lgobject-2.0"
	}
}


// KLib Workaround
// TODO: Remove with Kotlin/Native 0.3
task<Exec>("update-sources") {
	commandLine("./update-sources")
}