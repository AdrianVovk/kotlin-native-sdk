package sdk.util

import kotlinx.cinterop.*

// C imports for Date class
import c.time.time
import c.time.ctime

class Date {

   override fun toString() : String {
      return ctime(cValuesOf(time(null)))?.toKString()?.trim() ?: "DATE FAILED"
   }

}
