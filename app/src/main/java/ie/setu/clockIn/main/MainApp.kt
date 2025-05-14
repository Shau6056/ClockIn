package ie.setu.clockIn.main

import android.app.Application
import com.github.ajalt.timberkt.Timber
import ie.setu.clockIn.models.ClockLogModel
import ie.setu.clockIn.stores.ClockLogJSONStore

class MainApp : Application() {

    lateinit var clockLogStore: ClockLogJSONStore



    override fun onCreate() {
        super.onCreate()
        clockLogStore = ClockLogJSONStore(this)
        Timber.plant(Timber.DebugTree())

    }
}