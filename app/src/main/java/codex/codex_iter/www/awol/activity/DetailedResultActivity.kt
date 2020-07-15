package codex.codex_iter.www.awol.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.adapter.DetailedResultAdapter
import codex.codex_iter.www.awol.model.DetailResultData
import codex.codex_iter.www.awol.utilities.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class DetailedResultActivity : BaseThemedActivity() {
    private var msem: String? = null
    private var userm: SharedPreferences? = null
    private var result: String? = null
    private var l = 0
    private lateinit var detailResultData: Array<DetailResultData?>
    private var detailResultDataArrayList: ArrayList<DetailResultData>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailresults)
        val bundle = intent.extras
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDetailedResult)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Results"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
            setDisplayShowHomeEnabled(true)
        }

        if (bundle != null) {
            result = bundle.getString(Constants.RESULTS)
            msem = bundle.getString("Semester")
        }
        if (dark) {
            toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            recyclerView.setBackgroundColor(Color.parseColor("#141414"))
        } else {
           myDrawableCompact()
        }
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE)
        if (result != null) {
            val r = result!!.split("kkk".toRegex()).toTypedArray()
            result = r[0]
        }
        try {
            var jObj1: JSONObject? = null
            if (result != null) {
                jObj1 = JSONObject(result.toString())
                Log.d("resultdetail", jObj1.toString())
            }
            var arr: JSONArray? = null
            if (jObj1 != null) {
                val jOj2: JSONObject = jObj1.getJSONObject(msem.toString())
                arr = jOj2.getJSONArray("Semdata")
                Log.d("resultdetail", arr.toString())
            }
            if (arr != null) {
                l = arr.length()
            }
            detailResultData = arrayOfNulls(l)
            for (i in 0 until l) {
                var jObj: JSONObject? = null
                if (arr != null) {
                    jObj = arr.getJSONObject(i)
                }
                detailResultData[i] = DetailResultData()
                if (jObj != null) {
                    detailResultData[i]!!.subjectDesc = jObj.getString("subjectdesc")
                    detailResultData[i]!!.grade = jObj.getString("grade")
                    detailResultData[i]!!.earnedCredit = jObj.getString("earnedcredit")
                    detailResultData[i]!!.subjectCode = jObj.getString("subjectcode")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Error : ", e.toString())
        } finally {
            DetailResultData.detailResultData = detailResultData
            if (!Constants.Offlin_mode) {
                detailResultDataArrayList!!.addAll(Arrays.asList(*detailResultData).subList(0, l))
                saveAttendance(detailResultDataArrayList, msem)
            } else {
                getSavedAttendance(msem)
            }
            val resultAdapter = DetailedResultAdapter(this, detailResultDataArrayList)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = resultAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
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

    private fun saveAttendance(attendanceDataArrayList: ArrayList<*>?, sem: String?) {
//        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DetailResult", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        Constants.offlineDataEditor = Constants.offlineDataPreference!!.edit()
        val gson = Gson()
        val json = gson.toJson(attendanceDataArrayList)
        Constants.offlineDataEditor!!.putString("StudentDetailResult$sem", json)
        Constants.offlineDataEditor!!.apply()
    }

    fun getSavedAttendance(sem: String?) {
//        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DetailResult", MODE_PRIVATE);
        val gson = Gson()
        val json = Constants.offlineDataPreference!!.getString("StudentDetailResult$sem", null)
        val type = object : TypeToken<ArrayList<DetailResultData?>?>() {}.type
        detailResultDataArrayList = gson.fromJson<ArrayList<DetailResultData>>(json, type)
        if (detailResultDataArrayList == null) {
            detailResultDataArrayList = ArrayList()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }
}