package ie.setu.clockIn.models

import kotlin.time.Duration

data class ClockLogModel (
    val type: String,
    val startime: Long,
    val endTime: Long,
    val durationMin: Long
)

//Long data type stores whole numbers like intergers but it can store bigger numbers because I am are using the currentTimeMillis
//using the Long type makes sense as int wont hold enough numbers and is good for timestamps etc