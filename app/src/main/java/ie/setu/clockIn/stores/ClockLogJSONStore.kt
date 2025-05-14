package ie.setu.clockIn.stores
import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import ie.setu.clockIn.helpers.*
import ie.setu.clockIn.models.ClockLogModel
import kotlinx.datetime.Clock
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "clockIns.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<ClockLogModel>>() {}.type

fun generateRandomId(): String {
    return UUID.randomUUID().toString()
}

class ClockLogJSONStore (private val context: Context) {

    var logs = mutableListOf<ClockLogModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    fun findAll(): List<ClockLogModel> = logs

     fun create(log: ClockLogModel) {
        log.id = generateRandomId()
        logs.add(log)
        serialize()
    }

    //put in just in case
    fun update(log: ClockLogModel) {
        val exist = logs.find { it.id == log.id }
        if(exist != null){
            logs[logs.indexOf(exist)] = log
            serialize()
        }
    }

    fun delete(log: ClockLogModel){
        logs.remove(log)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(logs, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        logs = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        logs.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

