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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.R
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
        //val personId = arguments?.getInt("personId")!!

        /*
        database.personDao().getPerson(personId).observe(viewLifecycleOwner, Observer { person ->

            binding.editTextName.setText(person.name)
            binding.editTextBirthDay.setText(person.birthDay)
            binding.editTextBirthMonth.setText(person.birthMonth)
            person.birthYear?.let { binding.editTextBirthYear.setText(it) }

        })*/

        binding.buttonSavePerson.setOnClickListener {
            val newName = binding.editTextName.text.toString()
            val newDay = binding.editTextBirthDay.text.toString().toInt()
            val newMonth = binding.editTextBirthMonth.text.toString().toInt()
            val newYear = binding.editTextBirthDay.text.toString().toInt()
            val newPerson = Person(0, newName, newDay, newMonth, newYear)
            //GlobalScope.launch { database.personDao().updatePerson(newPerson) }
            lifecycleScope.launch {
                database.personDao().addPerson(newPerson)
                val text = getString(R.string.person_edited)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                scheduleNotification(newPerson)

                findNavController().popBackStack()
            }

            findNavController().navigateUp()
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