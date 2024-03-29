package net.example.simplebirthdayapp.eventList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBindings
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.calendar.PersonClickListener
import net.example.simplebirthdayapp.calendar.args
import net.example.simplebirthdayapp.calendar.bundlePersonID
import net.example.simplebirthdayapp.data.MonthRecord
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.FragmentEventListBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit


class EventListFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var database: PersonDatabase

    private val monthRecordsAdapter = MonthRecordsAdapter(PersonClickListener {
        val navController = findNavController()
        val argBundle = bundleOf(bundlePersonID to it)
        setFragmentResult(args, argBundle)
        navController.navigate(R.id.action_EventListFragment_to_EditPersonFragment, argBundle)
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            database = PersonDatabase.getDatabase(container.context)
        }

        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthCardsList.apply {
            adapter = monthRecordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        database.personDao().getAllPeople().observe(viewLifecycleOwner, Observer { it ->
            if (it.isNotEmpty()){
                binding.emptyListMessage.text = ""
            }
            // sorted by how many days remain to the next birthday
            val sortedPeople = it.sortedBy { (365 + ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(LocalDate.now().year, it.birthMonth, it.birthDay))) % 365 }
            val monthRecordList = ArrayList<MonthRecord>()

            for (record in PeopleByMonth(sortedPeople)) {
                monthRecordList.add(record)
            }

            monthRecordsAdapter.data = monthRecordList.toList()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class PeopleByMonth(
        val people: List<Person>
    ) : Iterable<MonthRecord> {

        override fun iterator(): Iterator<MonthRecord> {
            return PeopleByMonthIterator()
        }

        inner class PeopleByMonthIterator : Iterator<MonthRecord> {
            var index = 0

            override fun hasNext(): Boolean {
                return index < people.size
            }

            override fun next(): MonthRecord {
                val peopleInMonth = ArrayList<Person>()
                val month = people[index].birthMonth

                while (index < people.size && month == people[index].birthMonth) {
                    peopleInMonth.add(people[index])
                    index++
                }

                return MonthRecord(Month.of(month), peopleInMonth.toList())
            }
        }
    }
}
