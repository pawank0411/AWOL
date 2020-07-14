package codex.codex_iter.www.awol.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import codex.codex_iter.www.awol.R

class UnderMaintenance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_undermaintenance)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}