package net.example.simplebirthdayapp

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import net.example.simplebirthdayapp.databinding.FragmentFirstBinding
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val view = binding.root

        // Tlačítko zpět
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Nastaví ikonu a zároveň umístění ikony uprostřed
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.cake)

        // Nastavení titulku action baru na prázdný řetězec
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        // Inicializace databáze
        database = PersonDatabase.getDatabase(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView = binding.textView
        textView.setOnClickListener {

            val selectedDate = textView.text.toString()
            //TODO if date is same as in db show more info

        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
