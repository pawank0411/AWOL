package codex.codex_iter.www.awol.reciever

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import codex.codex_iter.www.awol.MainActivity
import codex.codex_iter.www.awol.R
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationId = 100
    private val CHANNEL_ID = "my_channel_01"
    override fun onReceive(context: Context, intent: Intent) {
        val stop = context.getSharedPreferences("STOP", 0)
        val notification_stop = stop.getBoolean("STOP_NOTIFICATION", false)
        val sharedPreferences = context.getSharedPreferences("Notification_date", 0)
        val editor = sharedPreferences.edit()
        if (!notification_stop) {
            val localTime = Calendar.getInstance()
            val tz = TimeZone.getTimeZone("GMT+05:30")
            localTime.timeZone = tz
            val date = localTime.time
            val simpleDateFormat = SimpleDateFormat("HH", Locale.US)
            val present_time = simpleDateFormat.format(date).toInt()
            //Toast.makeText(context, String.valueOf(present_time), Toast.LENGTH_SHORT).show();
            val set_time = context.getSharedPreferences("Set_time", 0)
            val set_t = set_time.getInt("Set_Time", 0)
            //  Toast.makeText(context, String.valueOf(set_t), Toast.LENGTH_SHORT).show();
            if (present_time > set_t) {
                Toast.makeText(context, "Notifications set for tomorrow!", Toast.LENGTH_SHORT).show()
            } else {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val intent1 = Intent(context, MainActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    editor.clear()
                    editor.apply()
                    val name: CharSequence = "Notifications" // The user-visible name of the channel.
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                    mChannel.setSound(null, null)
                    mChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
                    notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                            .setSmallIcon(R.drawable.bell_ring)
                            .setContentIntent(PendingIntent.getActivity(context, 131314, intent1,
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                            .setContentTitle("Want to sleep more?")
                            .setContentText("Check your attendance.")
                            .setTicker("Check your attendance.")
                            .setChannelId(CHANNEL_ID)
                            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                            .setSound(null)
                            .setLights(Color.GREEN, 3000, 3000)
                            .setColor(Color.parseColor("#12921F"))
                            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                            .setAutoCancel(true)
                    notificationManager.createNotificationChannel(mChannel)
                    notificationManager.notify(notificationId, notificationBuilder!!.build())
                    Log.v(ContentValues.TAG, "Notification sent")
                    val simpleDat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                    val date_fired = simpleDat.format(date)
                    editor.putString("Date", date_fired)
                    editor.apply()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    editor.clear()
                    editor.apply()
                    notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                            .setSmallIcon(R.drawable.bell_ring)
                            .setContentIntent(PendingIntent.getActivity(context, 131314, intent1,
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                            .setContentTitle("Want to sleep more?")
                            .setContentText("Check your attendance.")
                            .setTicker("Check your attendance.")
                            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                            .setSound(null)
                            .setLights(Color.GREEN, 3000, 3000)
                            .setColor(Color.parseColor("#12921F"))
                            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                            .setAutoCancel(true)
                    notificationManager.notify(notificationId, notificationBuilder!!.build())
                    Log.v(ContentValues.TAG, "Notification sent")
                    val simpleDat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                    val date_fired = simpleDat.format(date)
                    editor.putString("Date", date_fired)
                    editor.apply()
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    editor.clear()
                    editor.apply()
                    notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                            .setSmallIcon(R.drawable.bell_ring)
                            .setContentIntent(PendingIntent.getActivity(context, 131314, intent1,
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                            .setContentTitle("Want to sleep more?")
                            .setContentText("Check your attendance.")
                            .setTicker("Check your attendance.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSound(null)
                            .setLights(Color.GREEN, 3000, 3000)
                            .setColor(Color.parseColor("#12921F"))
                            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                            .setAutoCancel(true)
                    notificationManager.notify(notificationId, notificationBuilder!!.build())
                    val simpleDat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                    val date_fired = simpleDat.format(date)
                    editor.putString("Date", date_fired)
                    editor.apply()
                }
            }
        }
    }
}