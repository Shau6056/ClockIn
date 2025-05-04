package ie.setu.clockIn.models

import android.location.Location
import android.net.Uri
import java.util.Date
import kotlin.time.Duration

//Had to set the below to have values as the and then when data is added it will change

data class ClockLogModel (
    val type: String = "",
    val startTime: Long = 0L,
    var endTime: Long = 0L,
    var durationMin: Long = 0L,
    var image: Uri = Uri.EMPTY,
    var clockInDate: String?= "Not Working",
    var location: String? = "Not Working"
)

//Long data type stores whole numbers like intergers but it can store bigger numbers because I am are using the currentTimeMillis
//using the Long type makes sense as int wont hold enough numbers and is good for timestamps etc

//we also need the clockindate and location they are both string? or they are null