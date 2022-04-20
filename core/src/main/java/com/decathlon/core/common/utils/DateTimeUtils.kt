package com.decathlon.core.common.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun getDateTimeFormatted(): String {
    val localTime = Calendar.getInstance().time
    val date: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val time: DateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return String.format("%sT%sZ",date.format(localTime), time.format(localTime))
}