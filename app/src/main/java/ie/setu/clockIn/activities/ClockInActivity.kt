package ie.setu.clockIn.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.ajalt.timberkt.Timber
import ie.setu.clockIn.models.ClockInModel
import ie.setu.clockinsystem.R
import ie.setu.clockinsystem.databinding.ActivityClockinBinding
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber.i
import java.time.format.DateTimeFormatter

class ClockInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClockinBinding
    private val clockInList = mutableListOf<ClockInModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClockinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        displayCurrentTimeDate()

        Timber.i { "Logging message" }


        binding.btnAdd.setOnClickListener()
        {
            
            var capturedTime = displayCurrentTimeDate().toString()
            

            val descriptionInput = binding.description.text.toString().takeIf { it.isNotBlank() }

            if(capturedTime.isNotEmpty())
            {
                val clockInInfo = ClockInModel(clockInTime = capturedTime, lateDescription = descriptionInput)
                clockInList.add(clockInInfo)

                i("Clock in Added: $capturedTime")
                for(i in clockInList.indices)
                {
                    i("Clock In [$i]: ${this.clockInList[i]}")
                }

            }
        }

    }

    //This method is used to display the current date and time
    private fun displayCurrentTimeDate() {
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val formatTimeDate = DateTimeFormatter.ofPattern("EEE - HH:mm - dd/MM/yyyy") //This will show three letters - time and date.
        val displayTimeDate = currentDateTime.toJavaLocalDateTime().format(formatTimeDate)

        binding.textDateTime.text = displayTimeDate

    }
}





