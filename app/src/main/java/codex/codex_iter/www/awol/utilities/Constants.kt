package codex.codex_iter.www.awol.utilities

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.theme.ThemeItem
import java.util.*

object Constants {
    var themeItems: MutableList<ThemeItem>? = null
    val themes: List<ThemeItem>?
        get() {
            if (themeItems != null) return themeItems
            themeItems = ArrayList()
            themeItems.add(ThemeItem(R.color.grey50, R.style.AppTheme_NoActionBar, false))
            themeItems.add(ThemeItem(R.color.colorPrimaryDark, R.style.AppTheme_Dark_NoActionBar, true))
            themeItems.add(ThemeItem(R.color.red, R.style.AppTheme_NoActionBar_Red, false))
            themeItems.add(ThemeItem(R.color.red, R.style.AppTheme_Dark_NoActionBar_Red, true))
            themeItems.add(ThemeItem(R.color.orange, R.style.AppTheme_NoActionBar_Orange, false))
            themeItems.add(ThemeItem(R.color.orange, R.style.AppTheme_Dark_NoActionBar_Orange, true))
            themeItems.add(ThemeItem(R.color.blue, R.style.AppTheme_NoActionBar_Blue, false))
            themeItems.add(ThemeItem(R.color.blue, R.style.AppTheme_Dark_NoActionBar_Blue, true))
            themeItems.add(ThemeItem(R.color.green, R.style.AppTheme_NoActionBar_Green, false))
            themeItems.add(ThemeItem(R.color.green, R.style.AppTheme_Dark_NoActionBar_Green, true))
            return themeItems
        }

    fun setDarkStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#141414")
        }
    }

    const val API = "apiUrl"
    const val DETAILS = "details"
    const val RESULTSTATUS = "resultStatus"
    const val SHOWRESULT = "showResult"
    const val SHOWLECTUURES = "showlectures"
    const val UNDERMAINTAINECE = "underMaintenance"
    const val REGISTRATION_NUMBER = "registrationNumber"
    const val RESULTS = "result"
    const val STUDENT_NAME = "studentName"
    const val LOGIN = "loginCheck"
    const val NOATTENDANCE = "noAttendance"
    const val STUDENTSEMESTER = "stynumber"
    const val STUDENTBRANCH = "branchdesc"
    const val REMOTE_CONFIG = "remote_config"
    const val READ_DATABASE = "read_database"
    const val READ_DATABASE2 = "read_database2"
    const val READ_DATABASE3 = "read_database3"
    const val CHECK_VISIBILITY = "check_visibility"
    const val VIDEOURL = "url"
    var Offlin_mode = false
    var offlineDataPreference: SharedPreferences? = null
    var offlineDataEditor: SharedPreferences.Editor? = null
}