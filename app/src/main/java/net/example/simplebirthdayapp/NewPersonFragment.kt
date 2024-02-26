package net.example.simplebirthdayapp

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.FragmentNewPersonBinding
import net.example.simplebirthdayapp.notification.AppNotification
import net.example.simplebirthdayapp.notification.idExtra
import net.example.simplebirthdayapp.notification.messageExtra
import net.example.simplebirthdayapp.notification.titleExtra
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import java.time.LocalDate
import java.util.Calendar

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
                    val text = getString(R.string.person_added)
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                    scheduleNotification(person)

                    findNavController().popBackStack()
                }
            }
        }

        return view
    }

    private fun scheduleNotification(person: Person) {
        val appContext = requireContext().applicationContext
        val intent = Intent(appContext, AppNotification::class.java)
        val name = person.name
        val title = getString(R.string.birthday_today, name)
        val message = getString(R.string.message_to_user)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(idExtra, person.id)

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            person.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

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
            // TODO: THINK ABOUT THE YEAR TO YEAR UPDATE
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
