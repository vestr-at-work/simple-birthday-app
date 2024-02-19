package net.example.simplebirthdayapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.databinding.FragmentFirstBinding
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import java.util.Calendar

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
    ): View? {
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

        // Inicializace kalendáře a textového pole
        val calendarView = binding.calendarView
        val textViewFirst = binding.textviewFirst

        // Načtení záznamů z databáze a zobrazení v kalendáři
        lifecycleScope.launch(Dispatchers.IO) {
            val people = database.personDao().getAllPeople().value
            if (people != null){
                for (person in people) {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, person.year)
                    cal.set(Calendar.MONTH, person.month - 1) // Calendar.MONTH is 0-based
                    cal.set(Calendar.DAY_OF_MONTH, person.day)
                    val millis = cal.timeInMillis
                    requireActivity().runOnUiThread {
                        calendarView.setDate(millis, true, true)
                    }
                }
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Zde můžete provést akce na základě vybraného data v kalendáři
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Například můžete aktualizovat textový obsah TextView s datem
            val formattedDate = "${dayOfMonth}/${month + 1}/${year}"
            textViewFirst.text = formattedDate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
