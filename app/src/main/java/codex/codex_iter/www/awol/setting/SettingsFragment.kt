package codex.codex_iter.www.awol.setting

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.preference.SwitchPreference
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.reciever.AlramReceiver
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : PreferenceFragment() {
    private var flag = true
    private var dark = false
    private val firebaseAnalytics: FirebaseAnalytics? = null
    var coordinatorLayout: CoordinatorLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
        val notifications = findPreference<Preference>("pref_notification") as SwitchPreference?
        //       if (!dark) {

//        }
        val preferences1 = context.getSharedPreferences("Dark", Context.MODE_PRIVATE)
        val white = preferences1.getBoolean("dark", false)
        if (white) {
            val title: Spannable = SpannableString(notifications!!.title.toString())
            title.setSpan(ForegroundColorSpan(Color.BLACK), 0, title.length, 0)
            notifications.title = title
        }
        val stop = context.getSharedPreferences("STOP", 0)
        val editor1 = stop.edit()
        val device_time = context.getSharedPreferences("Set_time", 0)
        val set_time = device_time.edit()
        val sharedPreferences = activity.getSharedPreferences("Notification_date", 0)
        val editor = sharedPreferences.edit()
        coordinatorLayout = activity.findViewById<View>(R.id.coordinator) as CoordinatorLayout
        val packageName = context.packageName
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Snackbar.make(coordinatorLayout!!, "Allow app to run in background to get Notifications", Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.RED).setAction("Allow") { openPowerSettings(context) }.show()
        }
        if (notifications != null && notifications.isChecked) {
            editor1.putBoolean("STOP_NOTIFICATION", false)
            editor1.apply()
        }
        if (notifications != null) {
            notifications.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val checked = newValue as Boolean
                if (checked) {
                    editor1.putBoolean("STOP_NOTIFICATION", false)
                    editor1.apply()
                    if (!flag) {
                        Snackbar.make(coordinatorLayout!!, "Notifications Enabled", Snackbar.LENGTH_SHORT).show()
                        val calendar = Calendar.getInstance()
                        val alram_time = Date()
                        calendar[Calendar.HOUR_OF_DAY] = 7
                        calendar[Calendar.MINUTE] = 0
                        calendar[Calendar.SECOND] = 0
                        val set_t = calendar[Calendar.HOUR_OF_DAY]
                        set_time.putInt("Set_Time", set_t)
                        set_time.apply()
                        val present_date = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                        val present_d = present_date.format(alram_time)
                        val fired_date = sharedPreferences.getString("Date", "")
                        if (fired_date != null && !fired_date.isEmpty()) {
                            if (fired_date != present_d) {
                                val intent = Intent(activity, AlramReceiver::class.java)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    val intent1 = Intent()
                                    val packageName = activity.packageName
                                    val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                                    if (pm.isDeviceIdleMode) {
                                        val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                        alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                    } else {
                                        val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                        alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                    }
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            }
                        } else {
                            var intent = Intent(activity, AlramReceiver::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val intent1 = Intent()
                                val packageName = activity.packageName
                                val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (pm.isDeviceIdleMode) {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            } else {
                                intent = Intent(activity, AlramReceiver::class.java)
                                val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                            }
                        }
                    } else {
                        Snackbar.make(coordinatorLayout!!, "Notifications Enabled", Snackbar.LENGTH_LONG).show()
                        /*Alram time*/
                        val calendar = Calendar.getInstance()
                        calendar[Calendar.HOUR_OF_DAY] = 7
                        calendar[Calendar.MINUTE] = 0
                        calendar[Calendar.SECOND] = 0
                        val set_t = calendar[Calendar.HOUR_OF_DAY]
                        set_time.putInt("Set_Time", set_t)
                        set_time.apply()
                        val date = Date()
                        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                        val present_d = simpleDateFormat.format(date)
                        val fired_date = sharedPreferences.getString("Date", null)
                        if (fired_date == null) {
                            var intent = Intent(activity, AlramReceiver::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val intent1 = Intent()
                                val packageName = activity.packageName
                                val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (pm.isDeviceIdleMode) {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            } else {
                                //AutoStartPermissionHelper.getInstance().getAutoStartPermission(getActivity();
                                intent = Intent(activity, AlramReceiver::class.java)
                                val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                            }
                        } else if (fired_date != present_d) {
                            var intent = Intent(activity, AlramReceiver::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val intent1 = Intent()
                                val packageName = activity.packageName
                                val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (pm.isDeviceIdleMode) {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            } else {
                                intent = Intent(activity, AlramReceiver::class.java)
                                val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                            }
                        }
                    }
                } else {
                    Snackbar.make(coordinatorLayout!!, "Notifications Disabled", Snackbar.LENGTH_LONG).show()
                    flag = false
                    editor1.putBoolean("STOP_NOTIFICATION", true)
                    editor1.apply()
                    val intent = Intent(activity, AlramReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, 0)
                    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager?.cancel(pendingIntent)
                }
                true
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {}
    private fun toggleTheme(darkTheme: Boolean) {
        val editor = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(PREF_DARK_THEME, darkTheme)
        dark = true
        editor.apply()
    }

    private fun openPowerSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        context.startActivity(intent)
    }

    companion object {
        private const val PREFS_NAME = "prefs"
        private const val PREF_DARK_THEME = "dark_theme"
    }
}