package net.example.simplebirthdayapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import net.example.simplebirthdayapp.databinding.FragmentFirstBinding
import java.util.Calendar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Zde můžete provést akce na základě vybraného data v kalendáři
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Například můžete aktualizovat textový obsah TextView s datem
            val formattedDate = "${dayOfMonth}/${month + 1}/${year}"
            binding.textviewFirst.text = formattedDate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}