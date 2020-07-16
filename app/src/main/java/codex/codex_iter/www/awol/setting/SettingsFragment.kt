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
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.reciever.AlarmReceiver
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class SettingsFragment : PreferenceFragmentCompat() {
    private var flag = true
    private var dark = false
    var coordinatorLayout: CoordinatorLayout? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
        val notifications = findPreference<Preference>("pref_notification") as SwitchPreference?

        val preferences1 = requireContext().getSharedPreferences("Dark", Context.MODE_PRIVATE)
        val white = preferences1.getBoolean("dark", false)
        if (white) {
            val title: Spannable = SpannableString(notifications!!.title.toString())
            title.setSpan(ForegroundColorSpan(Color.BLACK), 0, title.length, 0)
            notifications.title = title
        }
        val stop = requireContext().getSharedPreferences("STOP", 0)
        val editor1 = stop.edit()
        val device_time = requireContext().getSharedPreferences("Set_time", 0)
        val set_time = device_time.edit()
        val sharedPreferences = requireActivity().getSharedPreferences("Notification_date", 0)
        coordinatorLayout = requireActivity().findViewById<View>(R.id.coordinator) as CoordinatorLayout
        val packageName = requireContext().packageName
        val pm = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Snackbar.make(coordinatorLayout!!, "Allow app to run in background to get Notifications", Snackbar.LENGTH_INDEFINITE).
            setActionTextColor(Color.RED).setAction("Allow") { context?.let { it1 -> openPowerSettings(it1) } }.show()
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
                                val intent = Intent(activity, AlarmReceiver::class.java)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent()
                                    requireActivity().packageName
                                    val pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
                                    if (pm.isDeviceIdleMode) {
                                        val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                    } else {
                                        val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                    }
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            }
                        } else {
                            var intent = Intent(activity, AlarmReceiver::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Intent()
                                requireActivity().packageName
                                val pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (pm.isDeviceIdleMode) {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            } else {
                                intent = Intent(activity, AlarmReceiver::class.java)
                                val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                            }
                        }
                    } else {
                        Snackbar.make(coordinatorLayout!!, "Notifications Enabled", Snackbar.LENGTH_LONG).show()
                        /*Alarm time*/
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
                            var intent = Intent(activity, AlarmReceiver::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Intent()
                                requireActivity().packageName
                                val pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (pm.isDeviceIdleMode) {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            } else {
                                //AutoStartPermissionHelper.getInstance().getAutoStartPermission(getActivity();
                                intent = Intent(activity, AlarmReceiver::class.java)
                                val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                            }
                        } else if (fired_date != present_d) {
                            var intent = Intent(activity, AlarmReceiver::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Intent()
                                requireActivity().packageName
                                val pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (pm.isDeviceIdleMode) {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else {
                                    val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                                }
                            } else {
                                intent = Intent(activity, AlarmReceiver::class.java)
                                val pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                            }
                        }
                    }
                } else {
                    Snackbar.make(coordinatorLayout!!, "Notifications Disabled", Snackbar.LENGTH_LONG).show()
                    flag = false
                    editor1.putBoolean("STOP_NOTIFICATION", true)
                    editor1.apply()
                    val intent = Intent(activity, AlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, 0)
                    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(pendingIntent)
                }
                true
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {}

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openPowerSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        context.startActivity(intent)
    }

    companion object {
        private const val PREFS_NAME = "prefs"
    }
}