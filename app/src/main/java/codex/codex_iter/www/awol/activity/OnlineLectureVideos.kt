package codex.codex_iter.www.awol.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.activity.OnlineLectureVideos
import codex.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter
import codex.codex_iter.www.awol.model.Lecture
import codex.codex_iter.www.awol.utilities.Constants
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class OnlineLectureVideos : BaseThemedActivity(), OnlineLectureSubjectAdapter.OnItemClickListener {
    @BindView(R.id.recycler_view)
    var recyclerView: RecyclerView? = null

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @BindView(R.id.main_Layout)
    var mainLayout: ConstraintLayout? = null
    private var directLink: String? = null
    private var dialog: BottomSheetDialog? = null
    private var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lectures)
        ButterKnife.bind(this)
        sharedPreferences = getSharedPreferences(Constants.API, Context.MODE_PRIVATE)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Video Lectures"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
            setDisplayShowHomeEnabled(true)
        }

        if (dark) {
            toolbar!!.setTitleTextColor(ContextCompat.getColor(applicationContext,R.color.white))
            recyclerView!!.setBackgroundColor(Color.parseColor("#141414"))
        } else {
            myDrawableCompact()
        }
        val sharedPreferences = getSharedPreferences(Constants.STUDENT_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("SubjectLinks", null)
        val type = object : TypeToken<ArrayList<Lecture?>?>() {}.type
        var lectureArrayList = gson.fromJson<ArrayList<Lecture>>(json, type)
        if (lectureArrayList == null) {
            lectureArrayList = ArrayList()
        }
        lectureArrayList.sortWith(Comparator { lecture: Lecture, t1: Lecture -> lecture.name!!.compareTo(t1.name.toString()) })
        val lecturesAdapter = OnlineLectureSubjectAdapter(this, lectureArrayList, true, this)
        recyclerView!!.adapter = lecturesAdapter
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
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

    override fun onClicked(subject_name: String?, video_link: String?) {
        //Fetching Direct link from url of box
        showBottomSheetDialog()
        getDirectLink(sharedPreferences!!.getString(Constants.API, null)!!, video_link!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun getDirectLink(vararg param: String) {
        val queue = Volley.newRequestQueue(applicationContext)
        val jsonObjec = JSONObject()
        try {
            jsonObjec.put("link", param[1])
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, param[0] + "/fetch", jsonObjec,
                Response.Listener { response: JSONObject ->
                    try {
                        hideBottomSheetDialog()
                        directLink = response.getString("direct_url")
                        Log.d("link", directLink.toString())
                        if (directLink.toString().isNotEmpty()) {
                            val intent = Intent(this@OnlineLectureVideos, VideoPlayer::class.java)
                            intent.putExtra(Constants.VIDEOURL, directLink.toString())
                            startActivity(intent)
                        } else {
                            val snackbar = Snackbar.make(mainLayout!!, "Something went wrong, Please try again!", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                    } catch (e: JSONException) {
                        Log.d("error", Objects.requireNonNull(e.toString()))
                    }
                }, Response.ErrorListener { error: VolleyError ->
            hideBottomSheetDialog()
            Log.d("volleyerror", error.toString())
            when (error) {
                is ServerError -> {
                    val snackBar = Snackbar.make(mainLayout!!, "Something went wrong, Please try again!", Snackbar.LENGTH_SHORT)
                    snackBar.show()
                }
                is NetworkError -> {
                    val snackBar = Snackbar.make(mainLayout!!, "Cannot establish connection!", Snackbar.LENGTH_SHORT)
                    snackBar.show()
                }
                else -> {
                    val snackBar = Snackbar.make(mainLayout!!, "Cannot establish connection!!", Snackbar.LENGTH_SHORT)
                    snackBar.show()
                }
            }
        })
        queue.add(jsonObjectRequest)
    }

    private fun showBottomSheetDialog() {
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.bottomprogressbar, null)
        if (dialog == null) {
            dialog = BottomSheetDialog(this)
            dialog!!.setContentView(view)
            dialog!!.setCancelable(false)
        }
        dialog!!.show()
    }

    private fun hideBottomSheetDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }
}