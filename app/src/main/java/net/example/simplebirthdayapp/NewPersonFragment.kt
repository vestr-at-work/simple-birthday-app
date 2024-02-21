package net.example.simplebirthdayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.FragmentNewPersonBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import java.time.LocalDate

class NewPersonFragment : Fragment() {

    private var _binding: FragmentNewPersonBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPersonBinding.inflate(inflater, container, false)
        val view = binding.root

        database = PersonDatabase.getDatabase(requireContext())


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
