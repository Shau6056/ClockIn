package ie.setu.clockIn

import android.app.Application
import com.github.ajalt.timberkt.Timber

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}