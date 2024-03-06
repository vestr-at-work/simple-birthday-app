package net.example.simplebirthdayapp.eventList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.calendar.PersonClickListener
import net.example.simplebirthdayapp.data.Person
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BirthdayListAdapter(private val clickListener: PersonClickListener) : RecyclerView.Adapter<BirthdayListAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    var data: List<Person> = listOf()
        set(value) {
            field = value
            // Custom setter to let adapter know, data were changed. In production implementation, it
            // should call more suitable notify* method, which will be much more efficient.
            notifyDataSetChanged()
        }

    class ViewHolder(view: View, listener: PersonClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val avatar : ImageView
        val name: TextView
        val dayOfMonth: TextView
        val daysRemaining: TextView
        val personClickListener: PersonClickListener
        val editButton: Button
        var personId : Int

        init {
            // Define click listener for the ViewHolder's View
            avatar = view.findViewById(R.id.image_avatar)
            name = view.findViewById(R.id.text_name_month)
            dayOfMonth = view.findViewById(R.id.text_day_of_month)
            daysRemaining = view.findViewById(R.id.text_days_remaining_month)
            editButton = view.findViewById(R.id.button_edit)
            personClickListener = listener
            editButton.setOnClickListener(this)
            personId = 0
        }
        override fun onClick(view: View) {
            personClickListener.onPersonClick(personId)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.birthday_record_for_month, viewGroup, false)

        return ViewHolder(view, clickListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val person = data[position]

        viewHolder.personId = person.id

        viewHolder.name.text = person.name

        viewHolder.dayOfMonth.text = """${person.birthDay}.${person.birthMonth}."""

        val today = LocalDate.now()
        val birthday = LocalDate.of(today.year, person.birthMonth, person.birthDay)
        val daysRemaining = (365 + ChronoUnit.DAYS.between(today, birthday)) % 365 // TODO: FIND CONST OR WRITE OWN

        viewHolder.daysRemaining.text = if (daysRemaining != 1L) "in $daysRemaining days" else "in $daysRemaining day" // not in resources, hope not necessary
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size

}