package ie.setu.clockIn.activities
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import android.Manifest
import android.net.Uri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ie.setu.clockIn.models.ClockLogModel
import ie.setu.clockinsystem.databinding.ActivityMainBinding
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber.i
import java.time.format.DateTimeFormatter
import java.util.Locale


class MainActivity : NavActivity() {

    private lateinit var binding: ActivityMainBinding

    private val clockInList = mutableListOf<ClockLogModel>()
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var location: FusedLocationProviderClient


    companion object {
        const val REQUESTCODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2500)
        installSplashScreen()


        super.onCreate(savedInstanceState)

        location = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupBottomNavigation()
        displayCurrentTimeDate()
        registerMapCallback()
        checkPermissions()


        Timber.i { "Logging message" }

        binding.btnAdd.setOnClickListener()
        {

            val descriptionInput = binding.description.text.toString().takeIf { it.isNotBlank() }

            getUserLocationReadable(this) { area, latitude, longitude ->

                var capturedTime = displayCurrentTimeDate()
                val intent = Intent(this, ClockOutActivity::class.java)
                intent.putExtra("clockIndate", capturedTime)
                intent.putExtra("location", area)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                startActivity(intent)

                if (capturedTime.isNotEmpty()) {
                    val clockInInfo = ClockLogModel(
                        type = "Clock In",
                        startTime = System.currentTimeMillis(),
                        endTime = 0L,
                        durationMin = 0L,
                        image = Uri.EMPTY,
                         clockInDate = capturedTime,
                         location = area,
                         latitude = latitude,
                         longitude = longitude

                        )
                    clockInList.add(clockInInfo)


                    Timber.i { "Clock In Added: $clockInInfo" }
                    Timber.i { "Clock In button was clicked" }

                    clockInList.forEachIndexed { index, item ->
                        Timber.i { " [$index] → $item" }
                    }

                    Snackbar.make(
                        binding.root,
                        "Clocked in at $capturedTime",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    Toast.makeText(this, "CLICK WORKED", Toast.LENGTH_SHORT).show()
                    println("Array contents: $clockInList")
                }
            }
        }

    }

    //This method is used to display the current date and time
    private fun displayCurrentTimeDate(): String {
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val formatTimeDate =
            DateTimeFormatter.ofPattern("EEE - HH:mm - dd/MM/yyyy") //This will show three letters - time and date.
        val displayTimeDate = currentDateTime.toJavaLocalDateTime().format(formatTimeDate)

        binding.textDateTime.text = displayTimeDate

        return displayTimeDate
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { i("Map Loaded") }
    }

    //Before accessing the location this is checking and requesting the permissions
    private fun locationPermission(onLocationReady: (String) -> Unit) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, REQUESTCODE)
    }
    // context: Context is an Android obj that gives access to the system serves in this case location
    // We are passing the context this into the fuction so it know the context to use
    // The onLocationReady parameter is a Kotlin lambda callback - when the location is ready
    //We fetch the data for the current location and then return it as string
    // And the string is then passed into the variable called area.
    private fun getUserLocationReadable(context: Context, onLocationReady: (String, Double, Double) -> Unit) {

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        //if the permissions are granted or not
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {

            onLocationReady("Permission not granted", 0.0, 0.0)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val getGeo = Geocoder(context, Locale.getDefault())
                try {
                    val addressStored =
                        getGeo.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addressStored.isNullOrEmpty()) {
                        val address = addressStored[0].getAddressLine(0)
                        Timber.i { "Address found: $address" }
                        onLocationReady(address, location.latitude, location.longitude)
                    } else {
                        onLocationReady("Location is unavailable at this moment", 0.0,0.0)

                    }
                } catch (e: Exception) {
                    Timber.e { "It failed" }
                    onLocationReady("Failed :${e.message}", 0.0, 0.0)
                }
            }
        }
    }
    //
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUESTCODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PERMISSION_GRANTED }) {
                Timber.i { "Location granted" }

                getUserLocationReadable(this) { area,longitude, latitude->
                    Timber.i { " Location is: $area" }
                }
            } else {
                Timber.w { "Location issue permission IS DENIED ********* DID NOT WORK" }
                Toast.makeText(this, "Location was not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() {
        val permissionsGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

        if (!permissionsGranted) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUESTCODE
            )
        } else {
            getUserLocationReadable(this) { area, longitude, latitude ->
                Timber.i { "Location at launch: $area" }
            }
        }
    }
}











