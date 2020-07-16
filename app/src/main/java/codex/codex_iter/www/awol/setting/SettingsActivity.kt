package codex.codex_iter.www.awol.setting

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.activity.BaseThemedActivity
import java.util.*

class SettingsActivity : BaseThemedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupToolbar()
        setupPreferences()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Setting"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
            setDisplayShowHomeEnabled(true)
        }

        val preferences = getSharedPreferences("Dark", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        if (!dark) {
            editor.putBoolean("dark", true)
            editor.apply()
            myDrawableCompact()
        } else {
            editor.putBoolean("dark", false)
            editor.apply()
        }
    }

    private fun myDrawableCompact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            findViewById<Toolbar>(R.id.toolbar)?.apply {
                setTitleTextColor(ContextCompat.getColor(context,R.color.black))
                navigationIcon?.colorFilter = BlendModeColorFilter(ContextCompat.getColor(context,R.color.black),
                        BlendMode.SRC_ATOP)
            }
        } else {
            findViewById<Toolbar>(R.id.toolbar)?.apply {
                setTitleTextColor(ContextCompat.getColor(context,R.color.black))
                navigationIcon?.setColorFilter(ContextCompat.getColor(context,R.color.black), PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    private fun setupPreferences() {
        fragmentManager.beginTransaction().replace(R.id.settings_fragment, SettingsFragment()).commit()
    }
}