package codex.codex_iter.www.awol.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import codex.codex_iter.www.awol.MainActivity
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.application.CleanCacheApplication
import codex.codex_iter.www.awol.utilities.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    var pref: SharedPreferences? = null
    private var check = 0
    private var clear_data = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SplashTheme)
        pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val collectionReference = FirebaseFirestore.getInstance().collection(Constants.DETAILS)
        collectionReference.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
            if (queryDocumentSnapshots != null) {
                for (documentChange in queryDocumentSnapshots.documentChanges) {
                    check = Objects.requireNonNull(documentChange.document.getString("under_maintenance")).toString().toInt()
                    clear_data = Objects.requireNonNull(documentChange.document.getString("clear_data")).toString().toInt()
                    pref!!.edit().putInt("CHECK", check).apply()
                    if (clear_data > pref!!.getInt("clear_data", 0)) {
                        Toast.makeText(this, "Data Successfully cleared.", Toast.LENGTH_SHORT).show()
                        pref!!.edit().putInt("clear_data", clear_data).apply()
                        CleanCacheApplication.getInstance().clearApplicationData()
                    }
                }
            }
        }
        if (pref!!.getInt("CHECK", 0) == 1) {
            val intent = Intent(this@SplashScreenActivity, UnderMaintenance::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}