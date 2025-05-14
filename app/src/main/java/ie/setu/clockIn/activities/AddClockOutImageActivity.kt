package ie.setu.clockIn.activities
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.squareup.picasso.Picasso
import ie.setu.clockIn.main.MainApp
import ie.setu.clockIn.models.ClockLogModel
import ie.setu.clockinsystem.databinding.ActivityClockoutimageBinding
import timber.log.Timber.i


class AddClockOutImageActivity : NavActivity() {


    private lateinit var binding: ActivityClockoutimageBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private var selectedImageUri: Uri = Uri.EMPTY
    lateinit var clockLog: ClockLogModel
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityClockoutimageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp


        //now we are getting the clocktype from ClockOut activity
        val clockType = intent.getStringExtra("clockType") ?: "Unknown"
        val startTime = intent.getLongExtra("startTime", 0L)
        val clockIndate = intent.getStringExtra("clockIndate")
        val location = intent.getStringExtra("location")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)


        clockLog = ClockLogModel(
            type = clockType,
            startTime = startTime,
            clockInDate = clockIndate,
            location = location,
            longitude = longitude,
            latitude = latitude
        )

        registerImagePickerCallback()



        binding.btnTakePhoto.setOnClickListener {
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            imageIntentLauncher.launch(request)
        }


            binding.btnSaveImage.setOnClickListener() {
                clockLog.endTime = System.currentTimeMillis()
                val dayLength = (clockLog.endTime - clockLog.startTime) / 1000 / 60
                clockLog.durationMin = dayLength
                clockLog.image = selectedImageUri
                app.clockLogStore.create(clockLog)

                val intent = Intent(this, ClockLogHistoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

        }

        private fun registerImagePickerCallback() {
            imageIntentLauncher = registerForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                try {
                    if(uri != null) {
                        contentResolver
                            .takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )

                        selectedImageUri = uri
                        clockLog.image = uri

                        i("IMG :: ${clockLog.image}")
                        Picasso.get()
                            .load(clockLog.image)
                            .into(binding.clockOutImage)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
