package net.example.simplebirthdayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.FragmentEditPersonBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase

class EditPersonFragment : Fragment() {

    private var _binding: FragmentEditPersonBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase
    private var personId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPersonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = PersonDatabase.getDatabase(requireContext())

        // TODO: remove !!
        val personId = arguments?.getInt("personId")!!// Here I don't know, how to navigate
        // firstly edit nav_graph.xml


        database.personDao().getPerson(personId).observe(viewLifecycleOwner, Observer { person ->
            /*
            binding.editTextName.setText(person.name)
            binding.editTextBirthDay.setText(person.birthDay)
            binding.editTextBirthMonth.setText(person.birthMonth)
            person.birthYear?.let { binding.editTextBirthYear.setText(it)
             }
             */
        })

        binding.buttonSavePerson.setOnClickListener {
            val newName = binding.editTextName.text.toString()
            val newDay = binding.editTextBirthDay.text.toString().toInt()
            val newMonth = binding.editTextBirthMonth.text.toString().toInt()
            val newYear = binding.editTextBirthDay.text.toString().toInt()

            GlobalScope.launch {
                database.personDao().updatePerson(Person(personId, newName, newDay, newMonth, newYear))
            }

            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}