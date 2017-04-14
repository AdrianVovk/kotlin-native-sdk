package sdk.util

import time.*
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.toKString

class Date {

   override fun toString() : String {
      return ctime(cValuesOf(time(null)))?.toKString()?.trim() ?: "DATE FAILED"
   }

}
