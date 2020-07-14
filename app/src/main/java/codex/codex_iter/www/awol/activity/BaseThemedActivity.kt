package codex.codex_iter.www.awol.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import codex.codex_iter.www.awol.R

abstract class BaseThemedActivity : AppCompatActivity() {
    protected var preferences: SharedPreferences? = null
    protected var dark = false
    override fun setTheme(resId: Int) {
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val theme = getSharedPreferences("theme", 0)
        dark = theme.getBoolean(PREF_DARK_THEME, false)
        //        if (useDarkTheme && dark) {
//                super.setTheme(getDarkTheme());
//        }
//        else super.setTheme(getLightTheme());
        super.setTheme(theme.getInt(THEME, R.style.AppTheme_NoActionBar))
    }

    companion object {
        private const val PREF_DARK_THEME = "dark_theme"
        private const val PREFS_NAME = "prefs"
        private const val THEME = "theme_pref"
    }
}