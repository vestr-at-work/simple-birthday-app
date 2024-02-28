package net.example.simplebirthdayapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            NotificationScheduler.startNotificationSchedulerRepeating(context)

            val nowHour = Calendar.getInstance().get(Calendar.HOUR)
            val prefHour = PreferenceManager
                .getDefaultSharedPreferences(context.applicationContext)
                .getString("notification_hour", "10")!!.toInt()

            if (prefHour >= nowHour) {
                NotificationScheduler.startNotificationSchedulerOnce(context)
            }
        }
    }
}