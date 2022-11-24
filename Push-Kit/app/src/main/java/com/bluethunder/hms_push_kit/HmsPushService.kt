package com.bluethunder.hms_push_kit

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import org.json.JSONObject

class HmsPushService : HmsMessageService() {

    companion object {
        private const val TAG = "HmsPushService"
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        // handle new token
        //  send new token to server

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        // data object ( needs rest apis )
        handleNotificationFromDataObject(remoteMessage)

        // or

        // notification object ( default notification object )
        handleNotificationFromNotificationObject(remoteMessage)

    }

    private fun handleNotificationFromDataObject(remoteMessage: RemoteMessage?) {
        val dataString = remoteMessage!!.data
        val jsonData = JSONObject(dataString)
        val title = jsonData.getString("title")
        val description = jsonData.getString("description")

        sendNotification(title, description)
    }

    private fun handleNotificationFromNotificationObject(remoteMessage: RemoteMessage?) {
        val notification = remoteMessage!!.notification
        val title = notification.title
        val description = notification.body

        sendNotification(title, description)
    }

    private fun sendNotification(title: String?, description: String?) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val CHANNEL_ID = "Push-Kit-test"
        if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
            val mChannel = NotificationChannel(
                CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val notifyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }

        val notifyPendingIntent = PendingIntent.getActivity(
            this,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.purple_700))
                .setContentTitle(title).setAutoCancel(true)
                .setSound(defaultSoundUri).setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setDefaults(Notification.DEFAULT_ALL).setContentIntent(notifyPendingIntent)
        notificationBuilder.setContentText(description)
        notificationManager.notify(
            System.currentTimeMillis().toString(), 0, notificationBuilder.build()
        )
    }


}