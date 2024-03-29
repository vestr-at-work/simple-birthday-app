package net.example.simplebirthdayapp.personStorage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.calendar.args
import net.example.simplebirthdayapp.calendar.bundlePersonID
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.FragmentEditPersonBinding
import net.example.simplebirthdayapp.notification.AppNotification
import net.example.simplebirthdayapp.notification.idExtra
import net.example.simplebirthdayapp.notification.messageExtra
import net.example.simplebirthdayapp.notification.titleExtra
import net.example.simplebirthdayapp.topBarMenu.SettingsFragment
import java.time.LocalDate
import java.util.Calendar

// Could be done nicer then with simple ints, but it works
val daysInMonths = mapOf<Int, Int>(
    1 to 31,
    2 to 29,
    3 to 31,
    4 to 30,
    5 to 31,
    6 to 30,
    7 to 31,
    8 to 31,
    9 to 30,
    10 to 31,
    11 to 30,
    12 to 31
)

class EditPersonFragment : Fragment() {

    private var _binding: FragmentEditPersonBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPersonBinding.inflate(inflater, container, false)

        // Setting input filters
        binding.editTextBirthDayEdit.filters = Array<InputFilter>(1) { SettingsFragment.InputFilterMinMax(1, 31) }
        binding.editTextBirthMonthEdit.filters = Array<InputFilter>(1) { SettingsFragment.InputFilterMinMax(1, 12) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = PersonDatabase.getDatabase(requireContext())

        setFragmentResultListener(args) { requestKey, bundle ->
            val personId = bundle.getInt(bundlePersonID)

            database.personDao().getPerson(personId).observe(viewLifecycleOwner, Observer { person ->
                if (person != null) {
                  binding.editTextNameEdit.setText(person.name)
                  binding.editTextBirthDayEdit.setText(person.birthDay.toString())
                  binding.editTextBirthMonthEdit.setText(person.birthMonth.toString())
                  person.birthYear?.let { binding.editTextBirthYearEdit.setText(it.toString()) }
                }
            })

            binding.buttonSavePerson.setOnClickListener {
                val newName = binding.editTextNameEdit.text.toString()
                val newDayString = binding.editTextBirthDayEdit.text.toString()
                val newMonthString = binding.editTextBirthMonthEdit.text.toString()
                val newYearString = binding.editTextBirthYearEdit.text.toString()

                if (newName.isBlank() || newDayString.isBlank() && newMonthString.isBlank()) {
                    val text = getString(R.string.person_not_edited)
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                    return@setOnClickListener
                }

                val newDay = newDayString.toInt()
                val newMonth = newMonthString.toInt()
                val newYear = if (newYearString.isNotBlank())  newYearString.toInt() else null

                // We know that month will be in the correct range now, but null check for compiler
                val monthDays = daysInMonths[newMonth]
                if (monthDays != null) {
                    if (newDay > monthDays) {
                        val text = getString(R.string.person_not_edited)
                        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                        return@setOnClickListener
                    }
                }

                val newPerson = Person(personId, newName, newDay, newMonth, newYear)

                lifecycleScope.launch {
                    database.personDao().updatePerson(newPerson)
                }
                findNavController().navigateUp()

                val text = getString(R.string.person_edited)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }

            binding.buttonDeletePerson.setOnClickListener {
                // Here only primary key matters to the Room database
                val person = Person(personId, "", 1, 1, 0)
                lifecycleScope.launch {
                    database.personDao().deletePerson(person)
                }
                findNavController().navigateUp()

                val text = getString(R.string.person_deleted)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}