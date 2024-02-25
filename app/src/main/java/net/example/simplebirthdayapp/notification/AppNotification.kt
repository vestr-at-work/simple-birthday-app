package net.example.simplebirthdayapp.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import net.example.simplebirthdayapp.CHANNEL_ID
import net.example.simplebirthdayapp.R

const val idExtra = "idExtra"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class AppNotification : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.cake)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(intent.getIntExtra(idExtra, 0), notification)
    }
}