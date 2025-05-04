package ie.setu.clockIn.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.clockIn.MainApp
import ie.setu.clockIn.adapters.ClockLogAdapter
import ie.setu.clockinsystem.databinding.ActivityClocklogHistoryBinding

class ClockLogHistoryActivity : NavActivity() {

    private lateinit var clockLogHisBinding: ActivityClocklogHistoryBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clockLogHisBinding = ActivityClocklogHistoryBinding.inflate(layoutInflater)
        setContentView(clockLogHisBinding.root)
        setupBottomNavigation()

        //Toolbar title
        clockLogHisBinding.toolbar.title = "Clock In History"
        setSupportActionBar(clockLogHisBinding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        clockLogHisBinding.recyclerView.layoutManager = layoutManager
        clockLogHisBinding.recyclerView.adapter = ClockLogAdapter(app.clockLogs)
    }
}
