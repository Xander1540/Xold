package com.example.xold

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.xold.activities.ChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MyFcmService : FirebaseMessagingService(){

    private companion object{

        private const val TAG= "MY_FCM_TAG"

        private const val NOTIFICATION_CHANNEL_ID = "XOLD_CHANNEL_ID"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title= "${remoteMessage.notification?.title}"
        val body = "${remoteMessage.notification?.body}"
        val senderUid = "${remoteMessage.data["senderUid"]}"
        val notificationType = "${remoteMessage.data["notificationType"]}"

        Log.d(TAG, "onMessageReceived: title: $title")
        Log.d(TAG, "onMessageReceived: body: $body")
        Log.d(TAG, "onMessageReceived: senderUid: $senderUid")
        Log.d(TAG, "onMessageReceived: notificationType: $notificationType")

        showChatNotification(title, body, senderUid)
    }

    private fun showChatNotification(notificationTitle: String, notificationDescription: String, senderUid: String){

        val notificationId = Random().nextInt(3000)

        val  notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        setupNotificationChannel(notificationManager)

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("receiptUid", senderUid)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_xold)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())

    }

    private fun setupNotificationChannel(notificationManager: NotificationManager){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Chat Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Show Chat Notification"
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}