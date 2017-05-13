package sdk.util

import c.time.*
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.toKString

class Date {

   override fun toString() : String {
      return ctime(cValuesOf(time(null)))?.toKString()?.trim() ?: "DATE FAILED"
   }

}
