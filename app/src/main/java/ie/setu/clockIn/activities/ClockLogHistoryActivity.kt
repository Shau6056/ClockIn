package ie.setu.clockIn.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.i
import com.squareup.picasso.Picasso
import ie.setu.clockIn.main.MainApp
import ie.setu.clockIn.adapters.ClockLogAdapter
import ie.setu.clockIn.models.ClockLogModel
import ie.setu.clockinsystem.databinding.ActivityClocklogHistoryBinding
import timber.log.Timber.i


class ClockLogHistoryActivity : NavActivity() {

    private lateinit var clockLogHisBinding: ActivityClocklogHistoryBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private var logEdit: ClockLogModel? = null
    lateinit var app: MainApp
    lateinit var logs: MutableList<ClockLogModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as MainApp

        logs = app.clockLogStore.findAll().toMutableList()

        registerImagePicker()

        clockLogHisBinding = ActivityClocklogHistoryBinding.inflate(layoutInflater)
        setContentView(clockLogHisBinding.root)
        setupBottomNavigation()

        //Toolbar title is just simply set below a
        clockLogHisBinding.toolbar.title = "Clock In History"
        setSupportActionBar(clockLogHisBinding.toolbar)


        clockLogHisBinding.recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView()

    }

    private fun recyclerView() {
        val updatedLogs = app.clockLogStore.findAll()
        clockLogHisBinding.recyclerView.adapter = ClockLogAdapter(updatedLogs,
            edit = { log ->
                logEdit = log
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
                imageIntentLauncher.launch(intent)
            },
            delete = { log ->
                //  Log.d("DEBUG", "Delete clicked for: ${log.clockInDate}")
                app.clockLogStore.delete(log)
                recyclerView()
            })
    }

    private fun registerImagePicker() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val uri = result.data!!.data!!
                    logEdit?.let {
                        it.image = uri
                        app.clockLogStore.update(it)
                    }
                    recyclerView()
                }
            }
    }
}


