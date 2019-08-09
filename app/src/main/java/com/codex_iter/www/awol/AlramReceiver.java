package com.codex_iter.www.awol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class AlramReceiver extends BroadcastReceiver {
    @SuppressWarnings("FieldCanBeLocal")
    private NotificationCompat.Builder notificationBuilder;
    @SuppressWarnings("FieldCanBeLocal")
    private int notificationId = 100;
    @SuppressWarnings("FieldCanBeLocal")
    private String CHANNEL_ID = "my_channel_01";

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar localTime = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
        localTime.setTimeZone(tz);
        Date date = localTime.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        int present_time =   Integer.parseInt(simpleDateFormat.format(date));

        SharedPreferences set_time = context.getSharedPreferences("Set_time", 0);
        int set_t = set_time.getInt("Set_Time", 0);
        if (present_time > set_t){
            Toast.makeText(context, "Alram set for tommorrow!", Toast.LENGTH_SHORT).show();
        }else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Notifications";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setSound(null, null);
                notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(PendingIntent.getActivity(context, 131314, intent1,
                                PendingIntent.FLAG_UPDATE_CURRENT))
                        .setContentTitle("Want to sleep more?")
                        .setContentText("Check your attendance.")
                        .setTicker("Check your attendance.")
                        .setChannelId(CHANNEL_ID)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(null)
                        .setLights(Color.GREEN, 3000, 3000)
                        .setColor(Color.parseColor("#12921F"))
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setAutoCancel(true);

                notificationManager.createNotificationChannel(mChannel);
                notificationManager.notify(notificationId, notificationBuilder.build());
                Log.v(TAG, "Notification sent");
                final SharedPreferences sharedPreferences = context.getSharedPreferences("Notification_date", 0);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                SimpleDateFormat simpleDat = new SimpleDateFormat("dd-MM-yyyy");
                String date_fired = simpleDat.format(date);
                editor.putString("Date", date_fired);
                editor.apply();

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(PendingIntent.getActivity(context, 131314, intent1,
                                PendingIntent.FLAG_UPDATE_CURRENT))
                        .setContentTitle("Want to sleep more?")
                        .setContentText("Check your attendance.")
                        .setTicker("Check your attendance.")
                        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                        .setSound(null)
                        .setLights(Color.GREEN, 3000, 3000)
                        .setColor(Color.parseColor("#12921F"))
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setAutoCancel(true);
                notificationManager.notify(notificationId, notificationBuilder.build());
                Log.v(TAG, "Notification sent");
                final SharedPreferences sharedPreferences = context.getSharedPreferences("Notification_date", 0);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                SimpleDateFormat simpleDat = new SimpleDateFormat("dd-MM-yyyy");
                String date_fired = simpleDat.format(date);
                editor.putString("Date", date_fired);
                editor.apply();
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(PendingIntent.getActivity(context, 131314, intent1,
                                PendingIntent.FLAG_UPDATE_CURRENT))
                        .setContentTitle("Want to sleep more?")
                        .setContentText("Check your attendance.")
                        .setTicker("Check your attendance.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(null)
                        .setLights(Color.GREEN, 3000, 3000)
                        .setColor(Color.parseColor("#12921F"))
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setAutoCancel(true);
                notificationManager.notify(notificationId, notificationBuilder.build());
                final SharedPreferences sharedPreferences = context.getSharedPreferences("Notification_date", 0);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                SimpleDateFormat simpleDat = new SimpleDateFormat("dd-MM-yyyy");
                String date_fired = simpleDat.format(date);
                editor.putString("Date", date_fired);
                editor.apply();
            }
        }
    }
}
