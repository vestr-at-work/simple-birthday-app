package net.example.simplebirthdayapp.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import net.example.simplebirthdayapp.CHANNEL_ID
import net.example.simplebirthdayapp.R
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import java.time.LocalDate
import java.util.Date

class NotificationScheduler : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val database = PersonDatabase.getDatabase(context)
        val today = LocalDate.now()

        database.personDao().getPeopleByDate(today.dayOfMonth, today.monthValue)

    }
}