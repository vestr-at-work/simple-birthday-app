package net.example.simplebirthdayapp.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.databinding.FragmentCalendarBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase

const val args = "args"
const val bundlePersonID = "personId"

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase

    private val birthdayListDayAdapter = BirthdayListDayAdapter(PersonClickListener {
        val navController = findNavController()
        val argBundle = bundleOf(bundlePersonID to it)
        setFragmentResult(args, argBundle)
        navController.navigate(R.id.action_CalendarFragment_to_EditPersonFragment, argBundle)
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
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth.${month + 1}.$year"
            binding.textViewCalendar.text = selectedDate

            database.personDao().getPeopleByDate(dayOfMonth, month + 1).observe(viewLifecycleOwner
            ) {
                birthdayListDayAdapter.data = it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}