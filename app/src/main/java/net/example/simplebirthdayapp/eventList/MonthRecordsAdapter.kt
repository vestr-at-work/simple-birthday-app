package net.example.simplebirthdayapp.eventList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.data.MonthRecord

class MonthRecordsAdapter : RecyclerView.Adapter<MonthRecordsAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    var data: List<MonthRecord> = listOf()
    set(value) {
        field = value
        // Custom setter to let adapter know, data were changed. In production implementation, it
        // should call more suitable notify* method, which will be much more efficient.
        notifyDataSetChanged()
    }

    private var viewPool = RecyclerView.RecycledViewPool()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val monthLabel : TextView
        val birthdayRecyclerView: RecyclerView

        init {
            // Define click listener for the ViewHolder's View
            monthLabel = view.findViewById(R.id.card_month_label)
            birthdayRecyclerView = view.findViewById(R.id.birthday_list)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_month_record, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val monthRecord = data[position]

        viewHolder.monthLabel.text = monthRecord.month.name

        val layoutManager = object : LinearLayoutManager(
            viewHolder.birthdayRecyclerView.context,
            LinearLayoutManager.VERTICAL,
            false) { override fun canScrollVertically() = false }

        layoutManager.initialPrefetchItemCount = monthRecord.birthdays.size

        val birthdayListAdapter = BirthdayListAdapter()
        birthdayListAdapter.data = monthRecord.birthdays

        viewHolder.birthdayRecyclerView.layoutManager = layoutManager
        viewHolder.birthdayRecyclerView.adapter = birthdayListAdapter
        viewHolder.birthdayRecyclerView.setRecycledViewPool(viewPool)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size

}