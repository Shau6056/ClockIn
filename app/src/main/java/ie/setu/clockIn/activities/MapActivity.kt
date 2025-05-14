package ie.setu.clockIn.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.clockIn.main.MainApp
import ie.setu.clockIn.models.ClockLogModel
import ie.setu.clockinsystem.R
import ie.setu.clockinsystem.databinding.ActivityMapBinding

class MapActivity : NavActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as MainApp

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val setu = LatLng(52.245696, -7.139102)
        mMap.addMarker(MarkerOptions().position(setu).title("Marker in Waterford"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(setu, 16f))

        val logs = app.clockLogStore.findAll()
        getLocations(logs)
    }

    private fun getLocations(logs: List<ClockLogModel>) {
        for (log in logs) {
            val lat = log.latitude
            val long = log.longitude
            val title = log.clockInDate ?: "No Date"
          //  val type = log.type ?: "Type Unknown"

            if (lat != null && long != null) {
                val location = LatLng(lat, long)
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(title)
                        .snippet("Clocked in at this location.")
                )
            }
        }

    }
}