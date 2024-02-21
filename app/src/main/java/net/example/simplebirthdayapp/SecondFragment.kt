package net.example.simplebirthdayapp
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.example.simplebirthdayapp.databinding.FragmentSecondBinding
import net.example.simplebirthdayapp.data.Person
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)

        // Dummy data for events
        val events: List<Person> = generateDummyEvents()
        //TODO: get it from database, possible sorted by remaining days
        val sortedEvents = events.sortedBy { LocalDate.of(2024, it.birthMonth, it.birthDay) }
        for (event in sortedEvents) {
            val row = TableRow(requireContext())

            // Nastavit vlastnosti řádku
            val params = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            row.layoutParams = params
            row.gravity = Gravity.CENTER

            // Vytvořit textové zobrazení pro měsíc
            val monthTextView = TextView(requireContext())
            monthTextView.text = event.name + " : " + event.birthDay.toString() + ". " +
                    event.birthMonth.toString() + "."
            monthTextView.setPadding(16, 16, 32, 16)
            row.addView(monthTextView)

            val countdownTextView = TextView(requireContext())
            val today = LocalDate.of(2024, 2, 21)
            val endDate = LocalDate.of(2024, event.birthMonth, event.birthDay)
            var daysUntil = ChronoUnit.DAYS.between(today, endDate)
            if (daysUntil < 0){
                daysUntil += 365
            }
            countdownTextView.text = "Remaining days: $daysUntil"
            countdownTextView.setPadding(32, 16, 16, 16)
            row.addView(countdownTextView)

            // Přidejte další textová pole nebo obrazová pole podle potřeby

            // Barevné podbarvení řádků (např. sudé řádky můžou mít jinou barvu)
            if (sortedEvents.indexOf(event) % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#FFFF00")) // žlutá barva pro sudé řádky
            }
            else {
                row.setBackgroundColor(Color.parseColor("#FFA500")) // oranžová pro liché
            }
            tableLayout.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateDummyEvents(): List<Person> {
        // Generate dummy events for demonstration
        val events = mutableListOf<Person>()
        val currentYear = 2024 // Change to the current year

        for (month in 1..12) {
            for (day in 1..30 step 4) {
                events.add(Person(0, "Jméno", day, month, currentYear))
            }
        }

        return events
    }
}
