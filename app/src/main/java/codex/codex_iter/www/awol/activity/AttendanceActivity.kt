package codex.codex_iter.www.awol.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.text.Html
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import codex.codex_iter.www.awol.MainActivity
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.adapter.AttendanceAdapter
import codex.codex_iter.www.awol.model.AttendanceData
import codex.codex_iter.www.awol.setting.SettingsActivity
import codex.codex_iter.www.awol.theme.ThemeFragment
import codex.codex_iter.www.awol.utilities.Constants
import codex.codex_iter.www.awol.utilities.DownloadScrapFile
import codex.codex_iter.www.awol.utilities.FirebaseConfig
import codex.codex_iter.www.awol.utilities.ScreenshotUtils
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class AttendanceActivity : BaseThemedActivity(), NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.main_layout)
    var mainLayout: LinearLayout? = null

    @BindView(R.id.drawer_layout)
    var drawerLayout: DrawerLayout? = null

    @BindView(R.id.check_result)
    var checkResult: Button? = null

    @BindView(R.id.nav_view)
    var navigationView: NavigationView? = null

    @BindView(R.id.NA_content)
    var tv: TextView? = null

    @BindView(R.id.rl)
    var recyclerView: RecyclerView? = null

    @BindView(R.id.NA)
    var noAttendanceLayout: ConstraintLayout? = null

    @BindView(R.id.who_layout)
    var whoLayout: ConstraintLayout? = null

    @BindView(R.id.who_button)
    var whoButton: Button? = null

    @BindView(R.id.removetile)
    var removetile: ImageView? = null

    @BindView(R.id.heading)
    var heading: TextView? = null

    @BindView(R.id.heading_desp)
    var heading_desp: TextView? = null

    @BindView(R.id.whoCard)
    var cardView: CardView? = null

    @BindView(R.id.logo)
    var logo: ImageView? = null

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    private var result: String? = null
    private lateinit var attendanceData: Array<AttendanceData?>
    private var l = 0
    private var avgab = 0
    private var avgat = 0.0
    private lateinit var r: Array<String>
    private var attendanceDataArrayList: ArrayList<AttendanceData>? = ArrayList()
    private var code: String? = null
    private var studentSemester: String? = null
    private var sub: SharedPreferences? = null
    private var userm: SharedPreferences? = null
    private var studentNamePreferences: SharedPreferences? = null
    override var preferences: SharedPreferences? = null
    private var sharedPreferences: SharedPreferences? = null
    private var adapter: AttendanceAdapter? = null
    private var noAttendance = false
    private var api: String? = null
    private var showResult: String? = null
    private var showlectures: String? = null
    private var dialog: BottomSheetDialog? = null
    private var readDatabase = 0
    private val customMenu: Menu? = null
    var state = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
    var color = intArrayOf(
            Color.rgb(255, 46, 84),
            Color.BLACK
    )
    var csl = ColorStateList(state, color)
    var state2 = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
    var color2 = intArrayOf(
            Color.rgb(255, 46, 84),
            Color.GRAY
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.activity_action_bar)
        }
        val viewCus = supportActionBar!!.customView
        val title: MaterialTextView = viewCus.findViewById(R.id.title)
        val icon = viewCus.findViewById<ImageView>(R.id.image)
        val share = viewCus.findViewById<ImageView>(R.id.share)
        preferences = getSharedPreferences("CLOSE", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences!!.edit()
        Constants.offlineDataPreference = getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE)
        val bundle = intent.extras
        val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        if (pref.getBoolean("is_First_Run2", true)) {
            pref.edit().putBoolean("is_First_Run2", false).apply()
            whoLayout!!.visibility = View.GONE
        }
        if (bundle != null) {
           // val loginCheck = bundle.getBoolean(Constants.LOGIN)
            api = bundle.getString(Constants.API)
            noAttendance = bundle.getBoolean(Constants.NOATTENDANCE)
            result = bundle.getString(Constants.RESULTS)
        }
        val firebaseConfig = FirebaseConfig()
        val json = firebaseConfig.fetch_latest_news(this)
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.getInt("version") >= 1) {
                if (whoLayout!!.visibility == View.GONE && preferences!!.getInt("version", 0) < jsonObject.getInt("version")) {
                    whoLayout!!.visibility = View.VISIBLE
                }
                preferences!!.edit().putInt("version", jsonObject.getInt("version")).apply()
                whoButton!!.setOnClickListener { view: View? ->
                    val uri: Uri
                    try {
                        uri = Uri.parse(jsonObject.getString("link"))
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                heading!!.text = jsonObject.getString("news_title")
                heading_desp!!.text = jsonObject.getString("news_text")
                Picasso.get()
                        .load(jsonObject.getString("image_url"))
                        .placeholder(R.drawable.ic_image)
                        .into(logo)
                if (preferences!!.getBoolean("close", false)) {
                    whoLayout!!.visibility = View.GONE
                }
                removetile!!.setOnClickListener { view: View? ->
                    editor.putBoolean("close", true)
                    whoLayout!!.visibility = View.GONE
                    editor.apply()
                }
            } else {
                whoLayout!!.visibility = View.GONE
            }
        } catch (e: JSONException) {
            Log.d("error_cardtile", e.toString())
        }
        icon.setOnClickListener { view: View? -> drawerLayout!!.openDrawer(GravityCompat.START) }
        navigationView!!.setNavigationItemSelectedListener(this)
        if (dark) {
            cardView!!.setBackgroundColor(Color.parseColor("#141414"))
            heading!!.setTextColor(Color.parseColor("#FFFFFFFF"))
            heading_desp!!.setTextColor(Color.parseColor("#FFCCCCCC"))
            heading_desp!!.setTextColor(Color.parseColor("#FFCCCCCC"))
            recyclerView!!.setBackgroundColor(Color.parseColor("#141414"))
            title.setTextColor(Color.parseColor("#ffffff"))
        }
        share.setOnClickListener {
            if (noAttendance) {
                val snackBar = Snackbar.make(mainLayout!!, "Attendance is currently unavailable", Snackbar.LENGTH_SHORT)
                snackBar.show()
            } else {
                val bitmap = ScreenshotUtils.getScreenShot(recyclerView)
                if (bitmap != null) {
                    val save = ScreenshotUtils.getMainDirectoryName(this)
                    val file = ScreenshotUtils.store(bitmap, "screenshot.jpg", save)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        shareScreenshot(file)
                    } else {
                        shareScreenshotLow(file)
                    }
                }
            }
        }
        sharedPreferences = getSharedPreferences(Constants.API, Context.MODE_PRIVATE)
        val apiCollection = FirebaseFirestore.getInstance().collection(Constants.RESULTSTATUS)
        apiCollection.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, _: FirebaseFirestoreException? ->
            if (queryDocumentSnapshots != null) {
                for (documentChange in queryDocumentSnapshots.documentChanges) {
                    showResult = documentChange.document.getString(Constants.SHOWRESULT)
                    showlectures = documentChange.document.getString(Constants.SHOWLECTUURES)
                    readDatabase = Objects.requireNonNull(documentChange.document.getString("fetch_file")).toString().toInt()
                    sharedPreferences!!.edit().putString(Constants.SHOWRESULT, showResult).apply()
                    sharedPreferences!!.edit().putString(Constants.SHOWLECTUURES, showlectures).apply()
                    if (showlectures == "0") navigationView!!.menu.findItem(R.id.lecture).isVisible = false
                    if (sharedPreferences!!.getInt(Constants.READ_DATABASE, 0) < readDatabase) {
                        sharedPreferences!!.edit().putInt(Constants.READ_DATABASE, readDatabase).apply()
                        showBottomSheetDialog()
                        downloadFile()
                    }
                }
            }
        }
        studentNamePreferences = getSharedPreferences(Constants.STUDENT_NAME, Context.MODE_PRIVATE)
        val studentName = studentNamePreferences!!.getString(Constants.STUDENT_NAME, "")
        if (noAttendance) {
            navigationView!!.menu.findItem(R.id.pab).isVisible = false
            recyclerView!!.visibility = View.GONE
            noAttendanceLayout!!.visibility = View.VISIBLE
            if (dark) {
                tv!!.setTextColor(Color.parseColor("#FFFFFF"))
                mainLayout!!.setBackgroundColor(Color.parseColor("#141414"))
            } else {
                checkResult!!.setTextColor(Color.parseColor("#141414"))
                tv!!.setTextColor(Color.parseColor("#141414"))
            }
        }
        if (result != null) {
            r = result!!.split("kkk".toRegex()).toTypedArray()
            result = r[0]
        }
        avgab = 0
        avgat = 0.0
        sub = getSharedPreferences("sub",
                Context.MODE_PRIVATE)
        try {
            val jObj1 = JSONObject(result.toString())
            val arr = jObj1.getJSONArray("griddata")
            l = arr.length()
            attendanceData = arrayOfNulls(l)
            for (i in 0 until l) {
                val jObj = arr.getJSONObject(i)
                attendanceData[i] = AttendanceData()
                code = jObj.getString("subjectcode")
                val ck = updated(jObj, sub, code, i)
                attendanceData[i]!!.setSubjectCode(code.toString())
                attendanceData[i]!!.setSubject(jObj.getString("subject"))
                attendanceData[i]!!.setTheory(jObj.getString("Latt"))
                attendanceData[i]!!.setLab(jObj.getString("Patt"))
                attendanceData[i]!!.setUpdate(ck)
                attendanceData[i]!!.setPercent(jObj.getString("TotalAttandence"))
                attendanceData[i]!!.setBunk()
                avgat += jObj.getString("TotalAttandence").trim { it <= ' ' }.toDouble()
                avgab += attendanceData[i]!!.getAbsent()
                studentSemester = jObj.getString(Constants.STUDENTSEMESTER)
                sharedPreferences!!.edit().putString(Constants.STUDENTSEMESTER, studentSemester).apply()
            }
            avgat /= l.toDouble()
            avgab /= l
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            AttendanceData.attendanceData = attendanceData
            if (!Constants.Offlin_mode) {
                attendanceDataArrayList!!.addAll(Arrays.asList(attendanceData).subList(0, l))
            } else {
                savedAttendance
            }
            saveAttendance(attendanceDataArrayList)
            adapter = AttendanceAdapter(this, attendanceDataArrayList)
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager = LinearLayoutManager(this)
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            val headerView = navigationView!!.getHeaderView(0)
            if (!Constants.Offlin_mode) {
                editor.putInt("AveragePresent", avgat.toInt())
                editor.putString("AverageAbsent", avgab.toString())
                if (bundle != null) {
                    val regis = bundle.getString(Constants.REGISTRATION_NUMBER)
                    editor.putString("RegistrationNumber", regis)
                }
                editor.apply()
            }
            val name = headerView.findViewById<TextView>(R.id.name)
            val reg = headerView.findViewById<TextView>(R.id.reg)
            name.text = studentName
            val split = studentName!!.split("\\s+".toRegex()).toTypedArray()
            if (split[0].isNotEmpty()) {
                title.text = "Hi, " + convertToTitleCaseIteratingChars(split[0]) + "!"
            } else {
                title.text = "Home"
            }
            reg.text = preferences!!.getString("RegistrationNumber", null)
            val avat = headerView.findViewById<TextView>(R.id.avat)
            avat.text = preferences!!.getInt("AveragePresent", 0).toString() + "%"
            val avab = headerView.findViewById<TextView>(R.id.avab)
            avab.text = preferences!!.getString("AverageAbsent", null)
            checkResult!!.setOnClickListener { fetchResult() }
        }
    }

    fun downloadFile() {
        val storageReferenceData = FirebaseStorage.getInstance().reference.child("data.txt")
        val storageReferenceVideo = FirebaseStorage.getInstance().reference.child("video.txt")
        val downloadScrapFile = DownloadScrapFile(this@AttendanceActivity)
        storageReferenceData.downloadUrl.addOnSuccessListener { uri: Uri ->
            downloadScrapFile.newDownload(uri.toString(), "data", false, "")
            storageReferenceVideo.downloadUrl.addOnSuccessListener { uri1: Uri ->
                downloadScrapFile.newDownload(uri1.toString(), "video", false, "")
                hideBottomSheetDialog()
            }.addOnFailureListener { e: Exception ->
                hideBottomSheetDialog()
                Toast.makeText(this@AttendanceActivity, "Something went wrong.Please try again.", Toast.LENGTH_SHORT).show()
                Log.d("errorStorage", e.toString())
                finish()
            }
        }.addOnFailureListener { e: Exception ->
            hideBottomSheetDialog()
            Toast.makeText(this@AttendanceActivity, "Something went wrong.Please try again.", Toast.LENGTH_SHORT).show()
            Log.d("errorStorage", e.toString())
            finish()
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun saveAttendance(attendanceDataArrayList: ArrayList<*>?) {
        Constants.offlineDataEditor = Constants.offlineDataPreference!!.edit()
        val gson = Gson()
        val json = gson.toJson(attendanceDataArrayList)
        Constants.offlineDataEditor!!.putString("StudentAttendance", json)
        Constants.offlineDataEditor!!.apply()
    }

    private val savedAttendance: Unit
        get() {
            val snackbar = Snackbar.make(mainLayout!!, "Offline mode enabled", Snackbar.LENGTH_SHORT)
            snackbar.show()
            val gson = Gson()
            val json = Constants.offlineDataPreference!!.getString("StudentAttendance", null)
            val type = object : TypeToken<ArrayList<AttendanceData?>?>() {}.type
            attendanceDataArrayList = gson.fromJson<ArrayList<AttendanceData>>(json, type)
            if (attendanceDataArrayList == null) {
                attendanceDataArrayList = ArrayList()
            }
        }

    private fun fetchResult() {
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE)
        val u = userm!!.getString("user", "")
        val p = userm!!.getString("pass", "")
        getData(api!!, u!!, p!!)
        showBottomSheetDialog()
    }

    private fun getData(vararg param: String) {
        val sharedPreferences = application.getSharedPreferences("Result", Context.MODE_PRIVATE)
        val queue = Volley.newRequestQueue(applicationContext)
        val postRequest: StringRequest = object : StringRequest(Method.POST, param[0] + "/result",
                Response.Listener(fun(response: String) {
                    if (response == "900") {
                        hideBottomSheetDialog()
                        val snackbar = Snackbar.make(mainLayout!!, "Results not found", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    } else {
                        hideBottomSheetDialog()
                        val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                        var res = response
                        res += "kkk" + param[1]
                        intent.putExtra(Constants.RESULTS, res)
                        intent.putExtra(Constants.API, api)
                        startActivity(intent)
                    }
                }),
                Response.ErrorListener { error: VolleyError ->
                    hideBottomSheetDialog()
                    if (error is AuthFailureError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackBar = Snackbar.make(mainLayout!!, "Wrong Credentials!", Snackbar.LENGTH_SHORT)
                            snackBar.show()
                        } else {
                            val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is ServerError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackBar = Snackbar.make(mainLayout!!, "Wrong Credentials!", Snackbar.LENGTH_SHORT)
                            snackBar.show()
                        } else {
                            val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is NetworkError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackBar = Snackbar.make(mainLayout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackBar.show()
                        } else {
                            Log.e("Volley_error", error.toString())
                            val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackBar = Snackbar.make(mainLayout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackBar.show()
                        } else {
                            val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user"] = param[1]
                params["pass"] = param[2]
                return params
            }
        }
        queue.add(postRequest)
    }

    private fun updated(jObj: JSONObject, sub: SharedPreferences?, code: String?, i: Int): String {
        return if (sub!!.contains(code)) {
            val old = JSONObject(sub.getString(code, "").toString())
            val statusLg = getSharedPreferences("status", 0)
            val status = statusLg.getString("status", "")
            if (status == "0") {
                old.put("Latt", "")
                old.put("Patt", "")
                old.put("TotalAttandence", "")
            }
            if (old.getString("Latt") != jObj.getString("Latt") || old.getString("Patt") != jObj.getString("Patt")) {
                jObj.put("updated", Date().time)
                attendanceData[i]!!.setOldAttendance(old.getString("TotalAttandence"))
                vibratePhone()
                sub.edit()!!.putString(code, jObj.toString())
                sub.edit()!!.apply()
                "Just now"
            } else DateUtils.getRelativeTimeSpanString(old.getLong("updated"), Date().time, 0).toString()
        } else {
            jObj.put("updated", Date().time)
            vibratePhone()
            sub.edit()!!.putString(code, jObj.toString())
            sub.edit()!!.apply()
            "Just now"
        }
    }

    private fun Activity.vibratePhone() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(400)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun shareScreenshot(file: File?) {
        try {
            val uri = FileProvider.getUriForFile(this, this.applicationContext.packageName +
                    ".my.package.name.provider", file!!)
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "image/*"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text))
            intent.putExtra(Intent.EXTRA_STREAM, uri) //pass uri here
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
        } catch (e: FileUriExposedException) {
            Toast.makeText(this, "Something, went wrong.", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareScreenshotLow(file: File?) {
        val uri = Uri.fromFile(file) //Convert file path into Uri for sharing
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text))
        intent.putExtra(Intent.EXTRA_STREAM, uri) //pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
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

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            moveTaskToBack(true)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val editor: SharedPreferences.Editor = preferences!!.edit()
        when (item.itemId) {
            R.id.sa -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check this out: tiny.cc/iter_awol \n ")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            R.id.lecture -> {
                if (sharedPreferences!!.getString(Constants.SHOWLECTUURES, "0") != "0") {
                    val intent = Intent(this@AttendanceActivity, OnlineLectureSubjects::class.java)
                    when (sharedPreferences!!.getString(Constants.STUDENTSEMESTER, "1")) {
                        "1" -> studentSemester = "1st"
                        "2" -> studentSemester = "2nd"
                        "3" -> studentSemester = "3rd"
                        "4" -> studentSemester = "4th"
                        "5" -> studentSemester = "5th"
                        "6" -> studentSemester = "6th"
                        "7" -> studentSemester = "7th"
                        "8" -> studentSemester = "8th"
                    }
                    intent.putExtra(Constants.STUDENTSEMESTER, studentSemester)
                    intent.putExtra(Constants.READ_DATABASE2, readDatabase)
                    startActivity(intent)
                }
            }
            R.id.abt -> {
                val intent = Intent(this@AttendanceActivity, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.cd -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.github_url))))
            R.id.lgout -> {
                val binder = AlertDialog.Builder(this@AttendanceActivity)
                binder.setMessage("Do you want to logout ?")
                binder.setTitle(Html.fromHtml("<font color='#FF7F27'>Message</font>"))
                binder.setCancelable(false)
                binder.setPositiveButton(Html.fromHtml("<font color='#FF7F27'>Yes</font>")) { dialog: DialogInterface?, which: Int ->
                    sub!!.edit()!!.putBoolean("logout", true)
                    sub!!.edit()!!.apply()
                    studentNamePreferences!!.edit().clear().apply()
                    editor.putBoolean("close", false)
                    editor.apply()
                    //Clearing the saved data
                    Constants.offlineDataPreference!!.edit().clear().apply()
                    sharedPreferences!!.edit().clear().apply()
                    val intent3 = Intent(applicationContext, MainActivity::class.java)
                    intent3.putExtra("logout_status", "0")
                    startActivity(intent3)
                }
                binder.setNegativeButton(Html.fromHtml("<font color='#FF7F27'>No</font>")) { dialog: DialogInterface, which: Int -> dialog.cancel() }
                val alertDialog = binder.create()
                val window = alertDialog.window
                var wlp: WindowManager.LayoutParams? = null
                if (window != null) {
                    wlp = window.attributes
                }
                if (wlp != null) {
                    wlp.gravity = Gravity.BOTTOM
                }
                if (wlp != null) {
                    wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
                }
                if (window != null) {
                    window.attributes = wlp
                }
                alertDialog.show()
                val nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                nbutton.setBackgroundColor(Color.RED)
                val pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                pbutton.setBackgroundColor(Color.GREEN)
            }
            R.id.pab -> {
                val intent = Intent(this@AttendanceActivity, BunkActivity::class.java)
                startActivity(intent)
            }
            R.id.result -> if (sharedPreferences!!.getString(Constants.SHOWRESULT, "") == "0") {
                val snackBar = Snackbar.make(mainLayout!!, "We will be back within 3-4 days", Snackbar.LENGTH_LONG)
                snackBar.show()
            } else {
                fetchResult()
            }
            R.id.setting -> {
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.policy -> {
                val uri = Uri.parse("https://awol-iter.flycricket.io/privacy.html")
                val intent2 = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent2)
            }
            R.id.change_theme -> {
                drawerLayout!!.closeDrawer(GravityCompat.START)
                val fragment: ThemeFragment = ThemeFragment.Companion.newInstance()
                fragment.show(supportFragmentManager, "theme_fragment")
            }
        }
        return true
    }

    companion object {
        fun convertToTitleCaseIteratingChars(text: String?): String? {
            if (text == null || text.isEmpty()) {
                return text
            }
            val converted = StringBuilder()
            var convertNext = true
            var chh: Char? = null
            for (ch in text.toCharArray()) {
                when {
                    Character.isSpaceChar(ch) -> {
                        convertNext = true
                    }
                    convertNext -> {
                        chh = Character.toTitleCase(ch)
                        convertNext = false
                    }
                    else -> {
                        chh = Character.toLowerCase(ch)
                    }
                }
                converted.append(chh)
            }
            return converted.toString()
        }
    }
}