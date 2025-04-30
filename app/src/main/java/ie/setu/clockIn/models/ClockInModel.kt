package ie.setu.clockIn.models

import android.location.Location
import android.os.Parcelable

data class ClockInModel(
    var clockInTime: String,
    var lateDescription: String?,
    var location: String?

)


