package net.example.simplebirthdayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.FragmentNewPersonBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase

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

        database = PersonDatabase.getDatabase(requireContext())

        binding.buttonAddPerson.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val birthDay = binding.editTextBirthDay.text.toString()
            val birthMonth = binding.editTextBirthMonth.text.toString()
            val birthYear = binding.editTextBirthYear.text.toString()
            if (name.isNotBlank() && birthDay.isNotBlank() && birthMonth.isNotBlank()) {
                var person: Person
                if (birthYear.isNotBlank()){
                    person = Person(0, name, birthDay.toInt(), birthMonth.toInt(), birthYear.toInt())
                }
                else {
                    person = Person(0, name, birthDay.toInt(), birthMonth.toInt(), null)
                }

                lifecycleScope.launch {
                    database.personDao().addPerson(person)

                    Snackbar.make(binding.root, "@string/person_added", Snackbar.LENGTH_SHORT).show()

                    findNavController().popBackStack()
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
