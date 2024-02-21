package net.example.simplebirthdayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.GlobalScope
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
            val birthday = binding.editTextBirthday.text.toString()
            if (name.isNotBlank() && birthday.isNotBlank()) {
                val person = Person(0, name, birthday.toInt(), birthday.toInt(), birthday.toInt())
                //TODO
                GlobalScope.launch {
                    database.personDao().addPerson(person)
                }

                findNavController().popBackStack()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
