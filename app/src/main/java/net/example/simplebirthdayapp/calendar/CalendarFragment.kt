package net.example.simplebirthdayapp.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.databinding.FragmentCalendarBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase

    private val birthdayListDayAdapter = BirthdayListDayAdapter(PersonClickListener {
        // TODO: EDIT PERSON FRAGMENT
        val navController = findNavController()
        navController.navigate(R.id.action_CalendarFragment_to_EditPersonFragment)
        Snackbar.make(
            binding.root,
            "Lets go edit person with id " + it.toString(),
            Snackbar.LENGTH_SHORT
        ).show()
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        database = PersonDatabase.getDatabase(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.birthdayListDay.apply {
            adapter = birthdayListDayAdapter
            layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically() = false
            }
        }

        val calendarView = binding.calendarView
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth.${month + 1}.$year"
            binding.textViewCalendar.text = selectedDate

            database.personDao().getPeopleByDate(dayOfMonth, month + 1).observe(viewLifecycleOwner, Observer {
                birthdayListDayAdapter.data = it
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}