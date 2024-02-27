package net.example.simplebirthdayapp.personStorage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import java.time.LocalDate
import java.util.Calendar

class EditPersonFragment : Fragment() {

    private var _binding: FragmentEditPersonBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: PersonDatabase

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

        setFragmentResultListener(args) { requestKey, bundle ->
            val personId = bundle.getInt(bundlePersonID)

            var oldName = ""
            var oldDay = 1
            var oldMonth = 1
            var oldYear: Int? = null
            database.personDao().getPerson(personId).observe(viewLifecycleOwner, Observer { person ->
                binding.editTextNameEdit.setText(person.name)
                binding.editTextBirthDayEdit.setText(person.birthDay.toString())
                binding.editTextBirthMonthEdit.setText(person.birthMonth.toString())
                person.birthYear?.let { binding.editTextBirthYearEdit.setText(it.toString()) }
                oldName = person.name
                oldDay = person.birthDay
                oldMonth = person.birthMonth
                oldYear = person.birthYear
            })

            binding.buttonSavePerson.setOnClickListener {
                val newName = binding.editTextNameEdit.text.toString()
                val newDay = binding.editTextBirthDayEdit.text.toString().toInt()
                val newMonth = binding.editTextBirthMonthEdit.text.toString().toInt()
                val newYearString = binding.editTextBirthYearEdit.text.toString()
                val newYear = if (newYearString != "")  newYearString.toInt() else null
                val newPerson = Person(personId, newName, newDay, newMonth, newYear)
                /*if (newName.isNotBlank() &&
                    0 < newDay && newDay < 32 &&
                    0 < newMonth && newMonth < 12) {
                    newPerson = Person(personId, newName, newDay, newMonth, newYear)
                }
                else {
                    newPerson = Person(personId, oldName, oldDay, oldMonth, oldYear)
                }*/
                lifecycleScope.launch {
                    database.personDao().updatePerson(newPerson)

                    scheduleNotification(newPerson)

                    findNavController().popBackStack()
                }

                findNavController().navigateUp()

                val text = getString(R.string.person_edited)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }

            binding.buttonDeletePerson.setOnClickListener {
                val newName = binding.editTextNameEdit.text.toString()
                val newDay = binding.editTextBirthDayEdit.text.toString().toInt()
                val newMonth = binding.editTextBirthMonthEdit.text.toString().toInt()
                val newYear = binding.editTextBirthYearEdit.text.toString().toInt()
                val newPerson = Person(personId, newName, newDay, newMonth, newYear)
                lifecycleScope.launch {
                    database.personDao().deletePerson(newPerson)

                    findNavController().popBackStack()

                }
                findNavController().navigateUp()

                val text = getString(R.string.person_deleted)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun scheduleNotification(person: Person) {
        val appContext = requireContext().applicationContext
        val intent = Intent(appContext, AppNotification::class.java)
        val name = person.name
        val title = "$name has birthday today"
        val message = "Don't forget to congratulate!"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(idExtra, person.id)

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            person.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        Log.d("SimpleBirthdayApp", person.toString())

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getScheduleTime(person)
        // TODO: CHECK IF PERMISSIONS ARE GRANTED, IF NOT ASK FOR THEM
        if (ActivityCompat.checkSelfPermission(
                appContext,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
            //                                        grantResults: IntArray)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d("SimpleBirthdayApp", "Permissions not granted")
        }
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            DateUtils.YEAR_IN_MILLIS,
            pendingIntent
        )
    }

    private fun getScheduleTime(person: Person): Long {
        val minute = 0
        val hour = PreferenceManager
            .getDefaultSharedPreferences(requireContext().applicationContext)
            .getString("notification_hour", "10")!!.toInt()
        val day = person.birthDay
        val month = person.birthMonth

        val today = LocalDate.now()
        val birthday = LocalDate.of(today.year, person.birthMonth, person.birthDay)
        val year = if (today < birthday) today.year else today.year + 1

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}