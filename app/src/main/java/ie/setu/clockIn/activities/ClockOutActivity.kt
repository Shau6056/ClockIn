package ie.setu.clockIn.activities
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.ajalt.timberkt.Timber
import ie.setu.clockIn.models.ClockLogModel
import ie.setu.clockinsystem.R
import ie.setu.clockinsystem.databinding.ActivityClockoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



//We have our class called ClockOutActivity which inherits from the AppCompatActivity our parent class in this instance
//AppCompatActivity settings up the screen, manages layout and handles the activity
class ClockOutActivity: NavActivity() {

    private lateinit var clockOutBind: ActivityClockoutBinding

    val clockLog = mutableListOf<ClockLogModel>()
    //The onCreate is the first thing that Android will run when the screen opens
    override fun onCreate(savedInstanceState: Bundle?) {
        //Calling the super.onCreate prevent crashing and proper set up //so we are running the android built in setup before I apply the
        //below logic
        super.onCreate(savedInstanceState)

        clockOutBind = ActivityClockoutBinding.inflate(layoutInflater)
        setContentView(clockOutBind.root)
        setupBottomNavigation()

        //Connecting activity to the activity_clock_out.xml file
        //The setContentView is from the Activity class which the AppCompatActivity inherits from
        //R -> Res -> Resource file.layout(folder) get the activity_clockout this is what we are accessing
        setContentView(R.layout.activity_clockout)
        //Getting the buttons and there ids in the buttons in the res.layout folder
        val btnBreak = findViewById<Button>(R.id.btnBreak)
        val btnLunch = findViewById<Button>(R.id.btnLunch)
        val btnFinish = findViewById<Button>(R.id.btnFinish)
        val btnReturn = findViewById<Button>(R.id.btnReturn)
        val message = findViewById<TextView>(R.id.statusText)
        var currentClockType: String = ""
        var clockStartTime: Long? = null

        //Now we are giving the user the click listener for the buttons
        btnBreak.setOnClickListener {
            currentClockType = "Break"
            clockStartTime = System.currentTimeMillis()//store the break time
            val getCurrent = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(clockStartTime!!))

            message.text = "You started your break at $getCurrent"
            Toast.makeText(this, "You are now on Break", Toast.LENGTH_SHORT).show()
            message.visibility = View.VISIBLE
            btnBreak.visibility = View.GONE
            btnLunch.visibility = View.GONE
            btnFinish.visibility = View.GONE
            btnReturn.visibility = View.VISIBLE
        }

        btnReturn.setOnClickListener{
            val breakEnd = System.currentTimeMillis()
            if(clockStartTime != null){
                val getTimeWorked = breakEnd-clockStartTime!!
                val workedMin = (getTimeWorked/1000)/ 60

                val loggedTimes = ClockLogModel(
                    type = currentClockType,
                    startTime = clockStartTime!!,
                    endTime = breakEnd,
                    durationMin = workedMin,

                )

                clockLog.add(loggedTimes)
                Timber.i { "User returned from $currentClockType, gone for $workedMin minutes" }
                Toast.makeText(this, "You were gone for $workedMin", Toast.LENGTH_LONG).show()

            }
            else{
                Toast.makeText(this, "Break time NOT recorded ", Toast.LENGTH_SHORT).show()
            }

            btnReturn.visibility = View.GONE
            message.visibility = View.GONE
            btnBreak.visibility = View.VISIBLE
            btnLunch.visibility = View.VISIBLE
            btnFinish.visibility = View.VISIBLE
        }

        btnLunch.setOnClickListener{
            currentClockType = "Lunch"
            clockStartTime = System.currentTimeMillis()

            val getCurrent = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(clockStartTime!!))

            message.text = "You started your Lunch at $getCurrent"

            Toast.makeText(this, "You are now on Lunch", Toast.LENGTH_SHORT).show()

            message.visibility = View.VISIBLE
            btnBreak.visibility = View.GONE
            btnLunch.visibility = View.GONE
            btnFinish.visibility = View.GONE
            btnReturn.visibility = View.VISIBLE
        }
        btnFinish.setOnClickListener{

            Timber.i { "Finish button clicked – attempting to launch AddClockOutImageActivity" }
            //I have to pass the data collected into the clockOutImageActivity
            val intent = Intent(this, AddClockOutImageActivity::class.java)
            //We are sending in the clocktype as Finish
            intent.putExtra("clockType", "Finish")
            //And sending the start time the ?: 0L is this is just saying if the clockStartTime is not null then use this but if it is use 0L zero
            intent.putExtra("startTime",clockStartTime ?: 0L)
            //And we are sending in the intent to the next activity which is our AddClockOutImageActivity
            startActivity(intent)

        }




    }

}


