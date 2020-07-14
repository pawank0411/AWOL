package codex.codex_iter.www.awol.activity

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import codex.codex_iter.www.awol.R
import java.util.*

class AboutActivity : BaseThemedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = "About"
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull(supportActionBar)!!.elevation = 0f
        Objects.requireNonNull(supportActionBar).setDisplayShowHomeEnabled(true)
        if (!dark) {
            toolbar.setTitleTextColor(resources.getColor(R.color.black))
            Objects.requireNonNull(toolbar.navigationIcon).setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun openFacebook(view: View?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/codexiter/?ref=br_rs")))
    }

    fun openInstagram(view: View?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/codexiter?igshid=w8g2cfygo8sy")))
    }

    fun openGmail(view: View?) {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "codexiter@gmail.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for AWOL")
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(Intent.createChooser(emailIntent, null))
    }

    fun openGithub(view: View?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/codex-iter/AWOL")))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }
}