package ie.setu.clockIn.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.clockIn.models.ClockLogModel
import ie.setu.clockinsystem.databinding.CardClockLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//creating a adapter class to connect the list of clock models to the recyclerview

class ClockLogAdapter(
    private val logs: List<ClockLogModel>,
    private val edit: (ClockLogModel) -> Unit,
    private val delete: (ClockLogModel)-> Unit):
//Adapter expects view of type Mainholder
    RecyclerView.Adapter<ClockLogAdapter.MainHolder>()
{
            //creating to say a new viewholder need to be created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {

        //inflate this when each in the list
        val binding = CardClockLogBinding.inflate(LayoutInflater.from(parent.context),parent,false)

                //returns the viewholder with inflated layout
        return MainHolder(binding)
    }
    //needed this to bind the data from the model to the viewholder views
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
       val log = logs[holder.adapterPosition]
        holder.bind(log, edit, delete)

    }
    //returns the num of items
    override fun getItemCount(): Int = logs.size
//holds and binds the data to the compentents of the card
    class MainHolder(private val binding: CardClockLogBinding):
            RecyclerView.ViewHolder(binding.root){

                //Binds the data from the clockmodel objects to the interface
                fun bind(
                    log: ClockLogModel,
                    edit: (ClockLogModel) -> Unit,
                    delete: (ClockLogModel) -> Unit
                ) {
                    binding.date.text = "Date: ${log.clockInDate}"

                    val start = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.startTime))
                    val end = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.endTime))

                    binding.textClockTime.text = "Start: $start | End: $end"
                    binding.textLocation.text = "Location: ${log.location}"
                    binding.btnEdit.setOnClickListener{edit(log)}
                    binding.btnDelete.setOnClickListener{delete(log)}

                    if (!log.image.toString().isNullOrEmpty()) {
                        Picasso.get().load(log.image).into(binding.imageClock)
                    }
                }

}

        }