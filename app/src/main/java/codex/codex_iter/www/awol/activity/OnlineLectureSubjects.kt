package codex.codex_iter.www.awol.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter
import codex.codex_iter.www.awol.model.Lecture
import codex.codex_iter.www.awol.utilities.Constants
import codex.codex_iter.www.awol.utilities.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class OnlineLectureSubjects : BaseThemedActivity(), OnlineLectureSubjectAdapter.OnItemClickListener {
    @BindView(R.id.recycler_view)
    var recyclerView: RecyclerView? = null

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @BindView(R.id.main_Layout)
    var main_layout: ConstraintLayout? = null
    private val subjectName = ArrayList<Lecture>()
    private val subjectLinks = ArrayList<Lecture>()
    private var sharedPreferences: SharedPreferences? = null
    private var jsonVideosLinks: String? = null
    private var jsonSubjectNames: String? = null
    private var branch: String? = null
    private var dialog: BottomSheetDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lectures)
        ButterKnife.bind(this)
        val bundle = intent.extras
        jsonVideosLinks = Utils.getJsonFromStorage(applicationContext, "video.txt")
        jsonSubjectNames = Utils.getJsonFromStorage(applicationContext, "data.txt")
        sharedPreferences = getSharedPreferences(Constants.STUDENT_NAME, Context.MODE_PRIVATE)
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar).setTitle("Video Lectures")
        Objects.requireNonNull(supportActionBar).setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull(supportActionBar).elevation = 0f
        Objects.requireNonNull(supportActionBar).setDisplayShowHomeEnabled(true)
        if (dark) {
            toolbar!!.setTitleTextColor(resources.getColor(R.color.white))
            recyclerView!!.setBackgroundColor(Color.parseColor("#141414"))
        } else {
            toolbar!!.setTitleTextColor(resources.getColor(R.color.black))
            Objects.requireNonNull(toolbar!!.navigationIcon).setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        }
        branch = sharedPreferences.getString(Constants.STUDENTBRANCH, "")
        if (bundle != null) {
            val sem = bundle.getString(Constants.STUDENTSEMESTER)
            sharedPreferences.edit().putString(Constants.STUDENTSEMESTER, sem).apply()
        }
        getJSONdata("")
        val lecturesAdapter = OnlineLectureSubjectAdapter(this, subjectName, false, this)
        recyclerView!!.adapter = lecturesAdapter
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
    }

    fun getJSONdata(subname: String?) {
        showBottomSheetDialog()
        try {
            subjectName.clear()
            subjectLinks.clear()
            if (jsonVideosLinks != null && jsonSubjectNames != null && !jsonSubjectNames!!.isEmpty() && !jsonVideosLinks!!.isEmpty()) {
                val lectures = JSONObject(jsonVideosLinks)
                val subject = JSONObject(jsonSubjectNames)
                val semester = arrayOf("2nd", "3rd", "4th", "5th", "6th", "7th", "8th")
                for (s in semester) {
                    if (Objects.requireNonNull(sharedPreferences!!.getString(Constants.STUDENTSEMESTER, null)).trim { it <= ' ' } == s) {
                        val subjects = lectures.getJSONObject(s)
                        val su = subject.getJSONObject(s)
                        val key_subject = su.keys()
                        while (key_subject.hasNext()) {
                            val keybranch = key_subject.next()
                            Log.d("keys_branch", keybranch)
                            if (keybranch == branch) {
                                hideBottomSheetDialog()
                                val sem_no = subjects.keys()
                                val subjectsname = su.getJSONArray(keybranch)
                                while (sem_no.hasNext()) {
                                    val keysubject = sem_no.next()
                                    for (i in 0 until subjectsname.length()) {
                                        if (keysubject == subjectsname[i]) {
                                            val links = subjects.getJSONArray(keysubject)
                                            subjectName.add(Lecture(keysubject))
                                            if (subname == keysubject && !subname.isEmpty()) {
                                                subjectLinks.clear()
                                                for (j in 0 until links.length()) {
                                                    val json = links.getJSONObject(j)
                                                    subjectLinks.add(Lecture(json.getString("name"), json.getString("link")))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                hideBottomSheetDialog()
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show()
                finish()
            }
            if (subjectName.size == 0) {
                hideBottomSheetDialog()
                val no_lectures = findViewById<MaterialTextView>(R.id.no_lectures)
                recyclerView!!.visibility = View.GONE
                no_lectures.visibility = View.VISIBLE
            }
        } catch (e: JSONException) {
            hideBottomSheetDialog()
            Toast.makeText(this, "Please wait while we are fetching subject list.", Toast.LENGTH_LONG).show()
            finish()
            Log.d("error", e.toString())
        }
    }

    fun showBottomSheetDialog() {
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

    override fun onClicked(subject_name: String?, video_link: String?) {
        getJSONdata(subject_name)
        val intent = Intent(this, OnlineLectureVideos::class.java)
        val gson = Gson()
        val json = gson.toJson(subjectLinks)
        sharedPreferences!!.edit().putString("SubjectLinks", json).apply()
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }
}