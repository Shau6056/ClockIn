package ie.setu.clockIn.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import ie.setu.clockIn.models.ClockInModel
import ie.setu.clockinsystem.R
import ie.setu.clockinsystem.databinding.ActivityClockinBinding
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber.i
import java.time.format.DateTimeFormatter
import java.util.Locale

class ClockInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClockinBinding
    private val clockInList = mutableListOf<ClockInModel>()
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var location: FusedLocationProviderClient
    companion object {
        const val REQUESTCODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        location = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityClockinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        displayCurrentTimeDate()
        registerMapCallback()

        Timber.i { "Logging message" }

        binding.btnAdd.setOnClickListener()
        {
            var capturedTime = displayCurrentTimeDate()
            val descriptionInput = binding.description.text.toString().takeIf { it.isNotBlank() }

            getUserLocationReadable(this) { area ->

            if (capturedTime.isNotEmpty()) {
                val clockInInfo =
                    ClockInModel(
                        clockInTime = capturedTime,
                        lateDescription = descriptionInput,
                        location = area

                    )
                clockInList.add(clockInInfo)


                Timber.i { "Clock In Added: $clockInInfo" }
                Timber.i { "Clock In button was clicked" }

                clockInList.forEachIndexed { index, item ->
                    Timber.i { " [$index] → $item" }
                }

                Snackbar.make(binding.root, "Clocked in at $capturedTime", Snackbar.LENGTH_SHORT)
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
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, REQUESTCODE)
    }

    //context : Context is an android obj it gives access to the location systems that I need we are passing
    //this into the getUserLocationReadable Then the onLocation is using a lambda callback -> defined by Kotlin
    //This means that we are getting the location and when the location is ready it will be a string
    //Therefor when it is called below in area the string is a
    private fun getUserLocationReadable(context: Context, onLocationReady: (String) -> Unit){
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val fusedLocationClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        //if the permissions are granted or not
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            onLocationReady("Permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location !=null)
            {
                val getGeo = Geocoder(context, Locale.getDefault())
                try{
                    val addressStored = getGeo.getFromLocation(location.latitude, location.longitude, 1)
                    if(!addressStored.isNullOrEmpty()){
                        val address = addressStored[0].getAddressLine(0)
                        Timber.i {"Address found: $address"}
                        onLocationReady(address)
                    }else{
                        onLocationReady("Location is unavailable at this moment")

                    }
                }catch (e: Exception)
                {
                    Timber.e{"It failed"}
                    onLocationReady("Failed :${e.message}")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUESTCODE){
            if(grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
            Timber.i {"Location granted"}

            getUserLocationReadable(this){
                area->
                Timber.i{" Location is: $area"}
            }
        }else{
            Timber.w{"Location issue permission IS DENIED ********* DID NOT WORK"}
            Toast.makeText(this, "Location was not found", Toast.LENGTH_SHORT).show()
        }
    }





}





