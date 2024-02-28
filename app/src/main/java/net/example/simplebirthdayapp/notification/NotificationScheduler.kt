package net.example.simplebirthdayapp.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import net.example.simplebirthdayapp.CHANNEL_ID
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

const val NOTIFICATION_SCHEDULER_ID = -2

class NotificationScheduler : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val database = PersonDatabase.getDatabase(context)
        val today = LocalDate.now()

        val people = database.personDao().getPeopleByDateStatic(today.dayOfMonth, today.monthValue)

        for (person in people) {
            scheduleTodayBirthdayNotification(context, person)
        }
    }

    private fun scheduleTodayBirthdayNotification(context: Context, person: Person) {
        val appContext = context.applicationContext
        val intent = Intent(appContext, AppNotification::class.java)
        val name = person.name
        val title = context.getString(R.string.birthday_notification_today_title, name)
        val message = context.getString(R.string.birthday_notification_today_message)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(idExtra, person.id)

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            person.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val time = getTodayScheduleTime(context, person)

        scheduleNotification(context, pendingIntent, time)
    }

    private fun scheduleEarlyBirthdayNotification(context: Context, person: Person, daysInAdvance: Int) {
        val appContext = context.applicationContext
        val intent = Intent(appContext, AppNotification::class.java)
        val name = person.name
        val title = context.getString(R.string.birthday_notification_in_advance_title, name, daysInAdvance)
        val message = context.getString(R.string.birthday_notification_in_advance_message)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(idExtra, person.id)

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            person.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val time = getEarlyScheduleTime(context, person)

        scheduleNotification(context, pendingIntent, time)
    }

    private fun getEarlyScheduleTime(context: Context, person: Person): Long {
        // TODO: implement this function
        return 0L
    }

    private fun scheduleNotification(context: Context, pendingIntent: PendingIntent, time: Long) {
        val appContext = context.applicationContext

        val a2larmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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


        a2larmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun getTodayScheduleTime(context: Context, person: Person): Long {
        val minute = 0
        val hour = PreferenceManager
            .getDefaultSharedPreferences(context.applicationContext)
            .getString("notification_hour", "10")!!.toInt()
        val day = person.birthDay
        val month = person.birthMonth

        val today = LocalDate.now()
        val year = today.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }
}