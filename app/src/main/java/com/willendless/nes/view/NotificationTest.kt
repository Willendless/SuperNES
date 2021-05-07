package com.willendless.nes.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_notification_test.*

class NotificationTest : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_test)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // create and register notification channel
        val channel = NotificationChannel("important", "Important", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
        // create notification intent
        val intent = Intent(this, NotificationActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        sendNotice.setOnClickListener {
            // create notification
            val notification = NotificationCompat.Builder(this, "important")
                    .setContentTitle("Context Title")
                    .setContentText("This is a context text")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .build()
            // register notification
            manager.notify(1, notification)
        }
    }
}