package codex.codex_iter.www.awol.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUriExposedException
import android.os.Vibrator
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
import codex.codex_iter.www.awol.activity.AttendanceActivity
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
    var who_layout: ConstraintLayout? = null

    @BindView(R.id.who_button)
    var who_button: Button? = null

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
    private var attendanceData: Array<AttendanceData?>
    private var l = 0
    private var avgab = 0
    private var avgat = 0.0
    private var r: Array<String>
    var attendanceDataArrayList: ArrayList<AttendanceData>? = ArrayList()
    private var code: String? = null
    private var student_semester: String? = null
    private var sub: SharedPreferences? = null
    private var userm: SharedPreferences? = null
    private var studentnamePrefernces: SharedPreferences? = null
    private override var preferences: SharedPreferences? = null
    private var sharedPreferences: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    private var adapter: AttendanceAdapter? = null
    private var no_attendance = false
    private var api: String? = null
    private var showResult: String? = null
    private var showlectures: String? = null
    private var dialog: BottomSheetDialog? = null
    private var read_database = 0
    private val custom_menu: Menu? = null
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
        Objects.requireNonNull(supportActionBar).title = ""
        Objects.requireNonNull(this.supportActionBar).displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        Objects.requireNonNull(supportActionBar).setCustomView(R.layout.activity_action_bar)
        val view_cus = supportActionBar!!.customView
        val title: MaterialTextView = view_cus.findViewById(R.id.title)
        val icon = view_cus.findViewById<ImageView>(R.id.image)
        val share = view_cus.findViewById<ImageView>(R.id.share)
        preferences = getSharedPreferences("CLOSE", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        Constants.offlineDataPreference = getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE)
        val bundle = intent.extras
        val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        if (pref.getBoolean("is_First_Run2", true)) {
            pref.edit().putBoolean("is_First_Run2", false).apply()
            who_layout!!.visibility = View.GONE
        }
        if (bundle != null) {
            val logincheck = bundle.getBoolean(Constants.LOGIN)
            api = bundle.getString(Constants.API)
            no_attendance = bundle.getBoolean(Constants.NOATTENDANCE)
            result = bundle.getString(Constants.RESULTS)
        }
        val firebaseConfig = FirebaseConfig()
        val json = firebaseConfig.fetch_latest_news(this)
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.getInt("version") >= 1) {
                if (who_layout!!.visibility == View.GONE && preferences.getInt("version", 0) < jsonObject.getInt("version")) {
                    who_layout!!.visibility = View.VISIBLE
                }
                preferences.edit().putInt("version", jsonObject.getInt("version")).apply()
                who_button!!.setOnClickListener { view: View? ->
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
                if (preferences.getBoolean("close", false)) {
                    who_layout!!.visibility = View.GONE
                }
                removetile!!.setOnClickListener { view: View? ->
                    editor.putBoolean("close", true)
                    who_layout!!.visibility = View.GONE
                    editor.apply()
                }
            } else {
                who_layout!!.visibility = View.GONE
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
        share.setOnClickListener { view: View? ->
            if (no_attendance) {
                val snackbar = Snackbar.make(mainLayout!!, "Attendance is currently unavailable", Snackbar.LENGTH_SHORT)
                snackbar.show()
            } else {
                val bitmap = ScreenshotUtils.getScreenShot(recyclerView)
                if (bitmap != null) {
                    val save = ScreenshotUtils.getMainDirectoryName(this)
                    val file = ScreenshotUtils.store(bitmap, "screenshot.jpg", save)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        shareScreenshot(file)
                    } else {
                        shareScreenshot_low(file)
                    }
                }
            }
        }
        sharedPreferences = getSharedPreferences(Constants.API, Context.MODE_PRIVATE)
        val apiCollection = FirebaseFirestore.getInstance().collection(Constants.RESULTSTATUS)
        apiCollection.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
            if (queryDocumentSnapshots != null) {
                for (documentChange in queryDocumentSnapshots.documentChanges) {
                    showResult = documentChange.document.getString(Constants.SHOWRESULT)
                    showlectures = documentChange.document.getString(Constants.SHOWLECTUURES)
                    read_database = Objects.requireNonNull(documentChange.document.getString("fetch_file")).toInt()
                    sharedPreferences.edit().putString(Constants.SHOWRESULT, showResult).apply()
                    sharedPreferences.edit().putString(Constants.SHOWLECTUURES, showlectures).apply()
                    if (showlectures == "0") navigationView!!.menu.findItem(R.id.lecture).isVisible = false
                    if (sharedPreferences.getInt(Constants.READ_DATABASE, 0) < read_database) {
                        sharedPreferences.edit().putInt(Constants.READ_DATABASE, read_database).apply()
                        showBottomSheetDialog()
                        downloadfile()
                    }
                }
            }
        }
        studentnamePrefernces = getSharedPreferences(Constants.STUDENT_NAME, Context.MODE_PRIVATE)
        val studentName = studentnamePrefernces.getString(Constants.STUDENT_NAME, "")
        if (no_attendance) {
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
            val jObj1 = JSONObject(result)
            val arr = jObj1.getJSONArray("griddata")
            l = arr.length()
            attendanceData = arrayOfNulls(l)
            for (i in 0 until l) {
                val jObj = arr.getJSONObject(i)
                attendanceData[i] = AttendanceData()
                code = jObj.getString("subjectcode")
                val ck = Updated(jObj, sub, code, i)
                attendanceData[i].setCode(code)
                attendanceData[i].setSub(jObj.getString("subject"))
                attendanceData[i]!!.theory = jObj.getString("Latt")
                attendanceData[i]!!.lab = jObj.getString("Patt")
                attendanceData[i].setUpd(ck)
                attendanceData[i]!!.percent = jObj.getString("TotalAttandence")
                attendanceData[i]!!.setBunk()
                avgat += jObj.getString("TotalAttandence").trim { it <= ' ' }.toDouble()
                avgab += attendanceData[i].getAbsent().toInt()
                student_semester = jObj.getString(Constants.STUDENTSEMESTER)
                sharedPreferences.edit().putString(Constants.STUDENTSEMESTER, student_semester).apply()
            }
            avgat /= l.toDouble()
            avgab /= l
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            AttendanceData.Companion.attendanceData = attendanceData
            if (!Constants.Offlin_mode) {
                attendanceDataArrayList!!.addAll(Arrays.asList(*attendanceData).subList(0, l))
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
            if (!split[0].isEmpty()) {
                title.text = "Hi, " + convertToTitleCaseIteratingChars(split[0]) + "!"
            } else {
                title.text = "Home"
            }
            reg.setText(preferences.getString("RegistrationNumber", null))
            val avat = headerView.findViewById<TextView>(R.id.avat)
            avat.setText(preferences.getInt("AveragePresent", 0).toString() + "%")
            val avab = headerView.findViewById<TextView>(R.id.avab)
            avab.setText(preferences.getString("AverageAbsent", null))
            checkResult!!.setOnClickListener { view: View? -> fetchResult() }
        }
    }

    fun downloadfile() {
        val storageReference_data = FirebaseStorage.getInstance().reference.child("data.txt")
        val storageReference_video = FirebaseStorage.getInstance().reference.child("video.txt")
        val downloadScrapFile = DownloadScrapFile(this@AttendanceActivity)
        storageReference_data.downloadUrl.addOnSuccessListener { uri: Uri ->
            downloadScrapFile.newDownload(uri.toString(), "data", false, "")
            storageReference_video.downloadUrl.addOnSuccessListener { uri1: Uri ->
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
        Constants.offlineDataEditor.putString("StudentAttendance", json)
        Constants.offlineDataEditor.apply()
    }

    val savedAttendance: Unit
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

    fun fetchResult() {
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE)
        val u = userm.getString("user", "")
        val p = userm.getString("pass", "")
        getData(api!!, u!!, p!!)
        showBottomSheetDialog()
    }

    //
    //    public static int convertDpToPixel(float dp) {
    //        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    //        float px = dp * (metrics.densityDpi / 160f);
    //        return Math.round(px);
    //    }
    private fun getData(vararg param: String) {
        val sharedPreferences = application.getSharedPreferences("Result", Context.MODE_PRIVATE)
        val queue = Volley.newRequestQueue(applicationContext)
        val postRequest: StringRequest = object : StringRequest(Method.POST, param[0] + "/result",
                Response.Listener { response: String ->
                    if (response == "900") {
                        hideBottomSheetDialog()
                        val snackbar = Snackbar.make(mainLayout!!, "Results not found", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    } else {
                        hideBottomSheetDialog()
                        val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                        response += "kkk" + param[1]
                        intent.putExtra(Constants.RESULTS, response)
                        intent.putExtra(Constants.API, api)
                        startActivity(intent)
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideBottomSheetDialog()
                    if (error is AuthFailureError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackbar = Snackbar.make(mainLayout!!, "Wrong Credentials!", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is ServerError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackbar = Snackbar.make(mainLayout!!, "Wrong Credentials!", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is NetworkError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackbar = Snackbar.make(mainLayout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            Log.e("Volley_error", error.toString())
                            val intent = Intent(this@AttendanceActivity, ResultActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            val snackbar = Snackbar.make(mainLayout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackbar.show()
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

    @Throws(JSONException::class)
    private fun Updated(jObj: JSONObject, sub: SharedPreferences?, code: String?, i: Int): String {
        return if (sub!!.contains(code)) {
            val old = JSONObject(sub.getString(code, ""))
            val status_lg = getSharedPreferences("status", 0)
            val status = status_lg.getString("status", "")
            if (status == "0") {
                old.put("Latt", "")
                old.put("Patt", "")
                old.put("TotalAttandence", "")
            }
            if (old.getString("Latt") != jObj.getString("Latt") || old.getString("Patt") != jObj.getString("Patt")) {
                jObj.put("updated", Date().time)
                attendanceData[i].setOld(old.getString("TotalAttandence"))
                edit = sub.edit()
                val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                v?.vibrate(400)
                edit.putString(code, jObj.toString())
                edit.apply()
                "Just now"
            } else DateUtils.getRelativeTimeSpanString(old.getLong("updated"), Date().time, 0).toString()
        } else {
            jObj.put("updated", Date().time)
            edit = sub.edit()
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v?.vibrate(400)
            edit.putString(code, jObj.toString())
            edit.commit()
            "Just now"
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

    fun shareScreenshot_low(file: File?) {
        val uri = Uri.fromFile(file) //Convert file path into Uri for sharing
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text))
        intent.putExtra(Intent.EXTRA_STREAM, uri) //pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
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
                        "1" -> student_semester = "1st"
                        "2" -> student_semester = "2nd"
                        "3" -> student_semester = "3rd"
                        "4" -> student_semester = "4th"
                        "5" -> student_semester = "5th"
                        "6" -> student_semester = "6th"
                        "7" -> student_semester = "7th"
                        "8" -> student_semester = "8th"
                    }
                    intent.putExtra(Constants.STUDENTSEMESTER, student_semester)
                    intent.putExtra(Constants.READ_DATABASE2, read_database)
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
                    edit = sub!!.edit()
                    edit.putBoolean("logout", true)
                    edit.apply()
                    studentnamePrefernces!!.edit().clear().apply()
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
                val snackbar = Snackbar.make(mainLayout!!, "We will be back within 3-4 days", Snackbar.LENGTH_LONG)
                snackbar.show()
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
            for (ch in text.toCharArray()) {
                if (Character.isSpaceChar(ch)) {
                    convertNext = true
                } else if (convertNext) {
                    ch = Character.toTitleCase(ch)
                    convertNext = false
                } else {
                    ch = Character.toLowerCase(ch)
                }
                converted.append(ch)
            }
            return converted.toString()
        }
    }
}