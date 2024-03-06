package net.example.simplebirthdayapp.personStorage

import android.os.Bundle
import android.text.InputFilter
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.FragmentNewPersonBinding
import net.example.simplebirthdayapp.topBarMenu.SettingsFragment

class NewPersonFragment : Fragment() {

    private var _binding: FragmentNewPersonBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPersonBinding.inflate(inflater, container, false)
        val view = binding.root

        // Setting input filters
        binding.editTextBirthDay.filters = Array<InputFilter>(1) { SettingsFragment.InputFilterMinMax(1, 31) }
        binding.editTextBirthMonth.filters = Array<InputFilter>(1) { SettingsFragment.InputFilterMinMax(1, 12) }

        database = PersonDatabase.getDatabase(requireContext())

        binding.buttonAddPerson.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val birthDayString = binding.editTextBirthDay.text.toString()
            val birthMonthString = binding.editTextBirthMonth.text.toString()
            val birthYearString = binding.editTextBirthYear.text.toString()

            if (name.isBlank() || birthDayString.isBlank() || birthMonthString.isBlank()) {
                // Incorrect input
                val text = getString(R.string.person_not_added)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            var birthDay = birthDayString.toInt()
            val birthMonth = birthMonthString.toInt()
            val birthYear = if (birthYearString.isNotBlank()) birthYearString.toInt() else null

            // We know that month will be in the correct range now, but null check for compiler
            val monthDays = daysInMonths[birthMonth]
            if (monthDays != null) {
                if (birthDay > monthDays) {
                    // Incorrect input
                    val text = getString(R.string.person_not_added)
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                    return@setOnClickListener
                }
            }

            val person = Person(0, name, birthDay, birthMonth, birthYear)

            lifecycleScope.launch {
                database.personDao().addPerson(person)
            }

            findNavController().navigateUp()

            val text = getString(R.string.person_added)
            Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
