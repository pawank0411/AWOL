package codex.codex_iter.www.awol.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.activity.ResultActivity
import codex.codex_iter.www.awol.adapter.ResultAdapter
import codex.codex_iter.www.awol.model.ResultData
import codex.codex_iter.www.awol.utilities.Constants
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt

class ResultActivity : BaseThemedActivity(), ResultAdapter.OnItemClickListener {
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @BindView(R.id.main_layout)
    var main_layout: LinearLayout? = null

    @BindView(R.id.recyclerViewDetailedResult)
    var recyclerView: RecyclerView? = null
    private var userm: SharedPreferences? = null
    private var result: String? = null
    private var l = 0
    private lateinit var ld: Array<ResultData?>
    private var resultDataArrayList: ArrayList<ResultData>? = ArrayList()
    private var sem = 0
    private var totalCredit: String? = null
    private var sgpa: String? = null
    private var status: String? = null
    private var api: String? = null
    private var dialog: BottomSheetDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailresults)
        Constants.offlineDataPreference = getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Results"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
            setDisplayShowHomeEnabled(true)
        }

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE)
        val bundle = intent.extras
        if (dark) {
            toolbar!!.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            recyclerView!!.setBackgroundColor(Color.parseColor("#141414"))
        } else {
            myDrawableCompact()
        }
        if (bundle != null) {
            result = bundle.getString(Constants.RESULTS)
            api = bundle.getString(Constants.API)
            Log.d(Constants.RESULTS, result!!)
        }
        if (result != null) {
            val r = result!!.split("kkk".toRegex()).toTypedArray()
            result = r[0]
        }
        try {
            var jObj1: JSONObject? = null
            if (result != null) {
                jObj1 = JSONObject(result)
                Log.d("resultdetail", jObj1.toString())
            }
            var arr: JSONArray? = null
            if (jObj1 != null) {
                arr = jObj1.getJSONArray("data")
            }
            if (arr != null) {
                l = arr.length()
            }
            ld = arrayOfNulls(l)
            for (i in 0 until l) {
                var jObj: JSONObject? = null
                if (arr != null) {
                    jObj = arr.getJSONObject(i)
                }
                ld[i] = ResultData()
                if (jObj != null) {
                    ld[i]?.semesterDesc = jObj.getString("Semesterdesc")
                    ld[i]?.styNumber = jObj.getString("stynumber").toInt()
                    ld[i]?.fail = jObj.getString("fail")
                    ld[i]?.totalearnedCredit = jObj.getString("totalearnedcredit")
                }
                ld[i]?.sgpaR = jObj?.getString("sgpaR")
                ld[i]?.cgpaR = jObj?.getString("cgpaR")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Error : ", e.toString())
        } finally {
            ResultData.Companion.ld = ld
            if (!Constants.Offlin_mode) {
                resultDataArrayList!!.addAll(Arrays.asList(ld).subList(0, l))
            } else {
                savedAttendance
            }
            saveAttendance(resultDataArrayList)
            val resultAdapter = ResultAdapter(this, resultDataArrayList, this)
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.adapter = resultAdapter
            recyclerView!!.layoutManager = LinearLayoutManager(this)
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

    @SuppressLint("CommitPrefEdits")
    fun saveAttendance(resultDataArrayList: ArrayList<*>?) {
        Constants.offlineDataEditor = Constants.offlineDataPreference!!.edit()
        val gson = Gson()
        val json = gson.toJson(resultDataArrayList)
        Constants.offlineDataEditor?.putString("StudentResult", json)
        Constants.offlineDataEditor?.apply()
    }

    val savedAttendance: Unit
        get() {
            val gson = Gson()
            val json = Constants.offlineDataPreference!!.getString("StudentResult", null)
            val type = object : TypeToken<ArrayList<ResultData?>?>() {}.type
            resultDataArrayList = gson.fromJson<ArrayList<ResultData>>(json, type)
            if (resultDataArrayList == null) {
                resultDataArrayList = ArrayList()
            }
        }

    override fun onResultClicked(sem: Int, totalCredit: String?, status: String?, sgpa: String?) {
        this.totalCredit = totalCredit
        this.status = status
        this.sgpa = sgpa
        this.sem = sem
        val u = userm!!.getString("user", "")
        val p = userm!!.getString("pass", "")
        val s = sem.toString()
        Log.d("SEM", "onResultClicked: $s")
        getData(api!!, u!!, p!!, s)
        showBottomSheetDialog()
    }

    private fun getData(vararg param: String) {
        val queue = Volley.newRequestQueue(applicationContext)
        val postRequest: StringRequest = object : StringRequest(Method.POST, param[0] + "/detailedResult",
                Response.Listener { response: String ->
                    if (response == "169") {
                        hideBottomSheetDialog()
                        val snackbar = Snackbar.make(main_layout!!, "Results not found", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    } else {
                        hideBottomSheetDialog()
                        val intent = Intent(this@ResultActivity, DetailedResultActivity::class.java)
                        var res = response
                        res += "kkk" + param[1]
                        intent.putExtra("result", res)
                        intent.putExtra("Semester", sem.toString())
                        intent.putExtra("SGPA", sgpa)
                        intent.putExtra("TotalCredit", totalCredit)
                        intent.putExtra("Status", status)
                        startActivity(intent)
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideBottomSheetDialog()
                    if (error is AuthFailureError) {
                        if (Constants.offlineDataPreference!!.getString("StudentDetailResult$sem", null) == null) {
                            val snackbar = Snackbar.make(main_layout!!, "Wrong Credentials!", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            val intent = Intent(this@ResultActivity, DetailedResultActivity::class.java)
                            intent.putExtra("Semester", sem.toString())
                            startActivity(intent)
                        }
                    } else if (error is ServerError) {
                        if (Constants.offlineDataPreference!!.getString("StudentDetailResult$sem", null) == null) {
                            val snackbar = Snackbar.make(main_layout!!, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            val intent = Intent(this@ResultActivity, DetailedResultActivity::class.java)
                            intent.putExtra("Semester", sem.toString())
                            startActivity(intent)
                        }
                    } else if (error is NetworkError) {
                        Log.e("Volley_error", error.toString())
                        if (Constants.offlineDataPreference!!.getString("StudentDetailResult$sem", null) == null) {
                            val snackbar = Snackbar.make(main_layout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            val intent = Intent(this@ResultActivity, DetailedResultActivity::class.java)
                            intent.putExtra("Semester", sem.toString())
                            startActivity(intent)
                        }
                    } else {
                        if (Constants.offlineDataPreference!!.getString("StudentDetailResult$sem", null) == null) {
                            val snackbar = Snackbar.make(main_layout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            val intent = Intent(this@ResultActivity, DetailedResultActivity::class.java)
                            intent.putExtra("Semester", sem.toString())
                            startActivity(intent)
                        }
                    }
                }
        ) {
            //fix here
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user"] = param[1]
                params["pass"] = param[2]
                params["sem"] = param[3]
                return params
            }
        }
        queue.add(postRequest)
    }

    private fun showBottomSheetDialog() {
        //    private BottomSheetBehavior bottomSheetBehavior;
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.bottomprogressbar, null)
        if (dialog == null) {
            dialog = BottomSheetDialog(this)
            dialog!!.setContentView(view)
            dialog!!.setCancelable(false)
        }
        dialog!!.show()
    }

    fun hideBottomSheetDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }

    companion object {
        fun convertDpToPixel(dp: Float): Int {
            val metrics = Resources.getSystem().displayMetrics
            val px = dp * (metrics.densityDpi / 160f)
            return px.roundToInt()
        }
    }
}