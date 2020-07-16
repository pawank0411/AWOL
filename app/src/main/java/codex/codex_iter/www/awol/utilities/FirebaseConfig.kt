package codex.codex_iter.www.awol.utilities

import android.app.Activity
import android.content.Context
import android.util.Log
import codex.codex_iter.www.awol.R
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class FirebaseConfig {
    private val mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    fun fetch_latest_news(context: Context?): String {
        val new_value = mFirebaseRemoteConfig.getString("news_link")
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener((context as Activity?)!!) { task: Task<Boolean?>? -> }.addOnFailureListener { e: Exception -> Log.d("error", e.toString()) }
        Log.d("json", new_value)
        return new_value
    }

    init {
        val configBuilder = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(43200) //12 hrs
                .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configBuilder)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }
}