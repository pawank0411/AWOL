package codex.codex_iter.www.awol.setting

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
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
        Objects.requireNonNull(supportActionBar).title = "Settings"
        Objects.requireNonNull(supportActionBar).setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull(supportActionBar).elevation = 0f
        Objects.requireNonNull(supportActionBar).setDisplayShowHomeEnabled(true)
        val preferences = getSharedPreferences("Dark", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        if (!dark) {
            editor.putBoolean("dark", true)
            editor.apply()
            toolbar.setTitleTextColor(resources.getColor(R.color.black))
            Objects.requireNonNull(toolbar.navigationIcon).setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        } else {
            editor.putBoolean("dark", false)
            editor.apply()
        }
    }

    private fun setupPreferences() {
        fragmentManager.beginTransaction().replace(R.id.settings_fragment, SettingsFragment()).commit()
    }
}