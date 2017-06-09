package sdk.util.log

import sdk.System
import sdk.gen.BuildMetadata
import sdk.util.Date

/**
 * A delegate for creating a [Logger] for a given class.
 *
 * To use, follow this format:
 *    class SomeClass {
 *       val Log: Logger by logger()
 *    }
 * Then call any function necessary on `Log`. For example, `Log.info("Information")`
 *
 * @return A delegate for obtaining a Logger
 *
 * @author Adrian Vovk
 */
fun Any.logger(): Lazy<Logger> {
  return lazy {
    Logger("SIMPLE NAME", "FULL NAME")
  }
}

/**
 * A global [Logger] that fetches the current class's name.
 *
 * @author Adrin Vovk
 */
val Any.Log: Logger
  get() = logger().getValue(this, this::Log)

/**
 * Used as the default tag for any given class
 *
 * Equates to the simple name of the class (or, if using a [Logger], the registered class)
 *
 * @author Adrian Vovk
 */
val Any.defaultTag: String
  get() =  if (this is Logger) {
	    this.classSimpleName
    } else if (this is CustomTagLoggable && this.tag != null) {
      this.tag!!
    } else {
      "SIMPLE NAME"
    }

/**
 * Used as an alternative, fully resolved, tag for any given class
 *
 * Equates to the full name of the class (or, if using a [Logger], the registered class)
 *
 * @author Adrian Vovk
 */
val Any.fullNameTag: String
  get() = if (this is Logger) {
            this.classFullName
          } else if (this is CustomTagLoggable && this.tag != null) {
            this.tag!!
          } else {
            "FULL NAME"
          }

/**
 * Used to log verbose information into STDOUT
 *
 * This function can be called on all types. It has three common uses.
 * + Called with `verbose("Message to be logged")`.
 * In such a case, the tag defaults to the name of the calling class
 * + Called with `SomeObject.verbose("Message to be logged")`.
 * In such a case, the tag defaults to the name of the receiver. In this example it would be `SomeObject`
 * + Called using a [Logger]. See the documentation for more information.
 *
 * @receiver This function can be called on [Any] class
 * @param msg The message to be printed out
 * @param tag The tag, or the source of the message, to be printed out. Defaults to [defaultTag]
 * @see Logger For usage information when attached to a Logger
 *
 * @author Adrian Vovk
 */
fun Any.verbose(msg: String, tag: String = defaultTag) = println("[VERBOSE - $tag] (${Date()}): $msg")

/**
 * Used to log verbose information into STDOUT
 *
 * This function extends all types, so it can be called like so: `AnyObject.verbose("Message")`.
 * In such a case, the tag defaults to the name of the receiver. In this case the tag would be `AnyObject`
 * To use as a general purpose logger, just call `verbose("Message")`.
 * The tag will default to the name of the calling object.
 *
 * @receiver This function can be called on [Any] class
 * @param msg The message to be printed out
 * @param exception Prints out extra information about an exception. Defaults to `null`
 * @param tag The tag, or the source of the message, to be printed out. Defaults to [defaultTag]
 * @see Logger For usage information when attached to a Logger
 *
 * @author Adrian Vovk
 */
fun Any.error(msg: String, exception: Exception? = null, tag: String = defaultTag) {
  println("\u001B[31m[ERROR - $tag] (${Date()}): $msg\u001B[0m")
  exception?.printStackTrace()
}

/**
 * Used to log any general information into STDOUT
 *
 * This function can be called on all types. It has three common uses.
 * + Called with `info("Message to be logged")`.
 * In such a case, the tag defaults to the name of the calling class
 * + Called with `SomeObject.info("Message to be logged")`.
 * In such a case, the tag defaults to the name of the receiver. In this example it would be `SomeObject`
 * + Called using a [Logger]. See the documentation for more information.
 *
 * @receiver This function can be called on [Any] class
 * @param msg The message to be printed out
 * @param tag The tag, or the source of the message, to be printed out. Defaults to [defaultTag]
 * @see Logger For usage information when attached to a Logger
 *
 * @author Adrian Vovk
 */
fun Any.info(msg: String, tag: String = defaultTag) = println("[INFO - $tag] (${Date()}): $msg")

/**
 * Used to log debug information into STDOUT
 *
 * This function can be called on all types. It has three common uses.
 * + Called with `debug("Message to be logged")`.
 * In such a case, the tag defaults to the name of the calling class
 * + Called with `SomeObject.debug("Message to be logged")`.
 * In such a case, the tag defaults to the name of the receiver. In this example it would be `SomeObject`
 * + Called using a [Logger]. See the documentation for more information.
 *
 * Please note: This function will only print out anything if the program is run in debug mode.
 *
 * @receiver This function can be called on [Any] class
 * @param msg The message to be printed out
 * @param tag The tag, or the source of the message, to be printed out. Defaults to [defaultTag]
 * @see Logger For usage information when attached to a Logger
 *
 * @author Adrian Vovk
 */
fun Any.debug(msg: String, tag: String = defaultTag) { if (BuildMetadata.DEBUG) println("[DEBUG - $tag] (${Date()}): $msg") }

fun Any.critical(msg: String, tag: String = defaultTag) = println("\u001B[31m[CRITICAL - $tag] (${Date()}): $msg\u001B[0m")

fun Any.fatal(msg: String, exception: Exception? = null, tag: String = defaultTag, code: Int = 1) {
  println("\u001B[31m[FATAL - $tag] (${Date()}): $msg\u001B[0m")
  exception?.printStackTrace()

  System.exit(code)
}

/**
 * A class that fakes its default Tags for uses like `Log.info("Message")`
 *
 * Created using the [logger] delegate.
 *
 * @see v A shortcut for `verbose`
 * @see e A shortcut for `error`
 * @see i A shortcut for `info`
 * @see d A shortcut for `debug`
 * @see c A shortcut for `critical`
 * @see f A shortcut for `fatal`
 * @property classSimpleName Used instead of `defaultTag`
 * @property classFullName Used instead of `fullNameTag`
 *
 * @author Adrian Vovk
 */
class Logger(val classSimpleName: String, val classFullName: String) {

  /**
   * A shortcut for [verbose]
   *
   * @see verbose For full details on this function.
   *
   * @author Adrian Vovk
   */
  fun v(msg: String, tag: String = defaultTag) = verbose(msg, tag)

  /**
   * A shortcut for [error]
   *
   * @see error For full details on this function.
   *
   * @author Adrian Vovk
   */
  fun e(msg: String, exception: Exception? = null, tag: String = defaultTag) = error(msg, exception, tag)

  /**
   * A shortcut for [info]
   *
   * @see info For full details on this function.
   *
   * @author Adrian Vovk
   */
  fun i(msg: String, tag: String = defaultTag) = info(msg, tag)

  /**
   * A shortcut for [debug]
   *
   * @see debug For full details on this function.
   *
   * @author Adrian Vovk
   */
  fun d(msg: String, tag: String = defaultTag) = debug(msg, tag)

  /**
   * A shortcut for [critical]
   *
   * @see critical For full details on this function.
   *
   * @author Adrian Vovk
   */
  fun c(msg: String, tag: String = defaultTag) = critical(msg, tag)

  /**
   * A shortcut for [fatal]
   *
   * @see fatal For full details on this function.``
   *
   * @author Adrian Vovk
   */
  fun f(msg: String, exception: Exception? = null, tag: String = defaultTag, code: Int = 1) = fatal(msg, exception, tag, code)
}

interface CustomTagLoggable {
  var tag: String?
}
