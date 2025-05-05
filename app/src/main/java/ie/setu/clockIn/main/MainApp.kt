package ie.setu.clockIn.main

import android.app.Application
import com.github.ajalt.timberkt.Timber
import ie.setu.clockIn.models.ClockLogModel

class MainApp : Application() {
    val clockLogs = mutableListOf<ClockLogModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}