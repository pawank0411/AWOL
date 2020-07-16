package codex.codex_iter.www.awol

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import codex.codex_iter.www.awol.activity.AttendanceActivity
import codex.codex_iter.www.awol.activity.BaseThemedActivity
import codex.codex_iter.www.awol.utilities.Constants
import codex.codex_iter.www.awol.utilities.DownloadScrapFile
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.onesignal.OneSignal
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt

class MainActivity : BaseThemedActivity() {
    @BindView(R.id.mainLayout)
    var mainLayout: CoordinatorLayout? = null

    @BindView(R.id.user)
    var user: EditText? = null

    @BindView(R.id.pass)
    var pass: EditText? = null

    @BindView(R.id.login_button)
    var login: Button? = null

    @BindView(R.id.progress_bar)
    var progressBar: ProgressBar? = null

    @BindView(R.id.passordLayout)
    var passLayout: TextInputLayout? = null

    @BindView(R.id.bottomSheet_view)
    var bottomSheetView: ConstraintLayout? = null

    @BindView(R.id.hello)
    var welcomeMessage: TextView? = null

    private var userm: SharedPreferences? = null
    private var logout: SharedPreferences? = null
    private var apiUrl: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    private var track = false
    private var studentName: String? = null
    private var student_branch: String? = null
    private var api: String? = null
    private var new_message: String? = null
    override var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private val appUpdateManager: AppUpdateManager? = null
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var updated_version = 0
    private var current_version = 0

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        Constants.offlineDataPreference = getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE)
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE)
        logout = getSharedPreferences("sub",
                Context.MODE_PRIVATE)
        preferences = getSharedPreferences(Constants.STUDENT_NAME, Context.MODE_PRIVATE)
        apiUrl = getSharedPreferences(Constants.API, Context.MODE_PRIVATE)
        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout?>(bottomSheetView!!)
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        val handler = Handler()
        handler.postDelayed({ bottomSheetBehavior!!.setPeekHeight(convertDpToPixel(600f)) }, 400)
        bottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {}
            override fun onSlide(view: View, v: Float) {}
        })
        val extras = intent.extras
        var status: String? = ""
        if (extras != null) {
            status = extras.getString("logout_status")
        }
        val status_lg = getSharedPreferences("status", 0)
        val editor = status_lg.edit()
        editor.putString("status", status)
        editor.apply()
        login!!.setOnClickListener {
            val u = user!!.text.toString().trim { it <= ' ' }
            val p = pass!!.text.toString().trim { it <= ' ' }
            if (u == "" || p == "") {
                val snackbar = Snackbar.make(mainLayout!!, "Enter your Details", Snackbar.LENGTH_SHORT)
                snackbar.show()
            } else {
                progressBar!!.visibility = View.VISIBLE
                welcomeMessage!!.visibility = View.VISIBLE
                login!!.visibility = View.GONE
                user!!.isEnabled = false
                pass!!.isEnabled = false
                user!!.isFocusable = false
                pass!!.isFocusable = false
                passLayout!!.isPasswordVisibilityToggleEnabled = false
                if (!preferences!!.contains(Constants.STUDENT_NAME) || !preferences!!.contains(Constants.STUDENTBRANCH)) {
                    getName(api!!, u, p)
                } else {
                    getData(api, u, p)
                }
                edit = userm!!.edit()
                edit!!.putString("user", u)
                edit!!.putString(u + "pass", p)
                edit!!.putString("pass", p)
                edit!!.apply()
                edit = logout!!.edit()
                edit!!.putBoolean("logout", false)
                edit!!.apply()
            }
        }
        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            current_version = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val apiCollection = FirebaseFirestore.getInstance().collection(Constants.DETAILS)
        apiCollection.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
            if (queryDocumentSnapshots != null) {
                for (documentChange in queryDocumentSnapshots.documentChanges) {
                    api = documentChange.document.getString(Constants.API)
                    updated_version = Objects.requireNonNull(documentChange.document.getString("update_available")).toString().toInt()
                    new_message = documentChange.document.getString("what's_new")
                    edit = apiUrl!!.edit()
                    edit!!.putString(Constants.API, api)
                    edit!!.apply()
                    if (current_version in 1 until updated_version) {
                        askPermission()
                        val storageReference_data = FirebaseStorage.getInstance().reference.child("apk_version/").child("awol.apk")
                        storageReference_data.downloadUrl.addOnSuccessListener { uri: Uri ->
                            val downloadScrapFile = DownloadScrapFile(this@MainActivity)
                            downloadScrapFile.newDownload(uri.toString(), "awol", true, new_message)
                        }.addOnFailureListener { e1: Exception -> Log.e("error_version", e1.toString()) }
                    } else {
                        autoFill()
                    }
                }
            }
        }
    }

    fun autoFill() {
        if (userm!!.contains("user") && userm!!.contains("pass") && logout!!.contains("logout") && !logout!!.getBoolean("logout", false)) {
            user!!.isFocusable = false
            pass!!.isFocusable = false
            user!!.setText(userm!!.getString("user", ""))
            pass!!.setText(userm!!.getString("pass", ""))
            login!!.performClick()
        }
    }

    private fun getData(vararg param: String?) {
        if (param[0] == null) {
            param[0] = apiUrl!!.getString(Constants.API, "")
        }
        val queue = Volley.newRequestQueue(applicationContext)
        val postRequest: StringRequest = object : StringRequest(Method.POST, param[0].toString() + "/attendance",
                Response.Listener { response: String ->
                    if (response == "404") {
                        //User Credential wrong or user doesn't exists.
                        progressBar!!.visibility = View.INVISIBLE
                        welcomeMessage!!.visibility = View.GONE
                        login!!.visibility = View.VISIBLE
                        user!!.isEnabled = true
                        pass!!.isEnabled = true
                        passLayout!!.isPasswordVisibilityToggleEnabled = true
                        val snackbar = Snackbar.make(mainLayout!!, "Wrong credentials", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    } else if (response == "390") {
                        //Attendance not present
                        val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                        intent.putExtra(Constants.REGISTRATION_NUMBER, user!!.text.toString())
                        intent.putExtra(Constants.NOATTENDANCE, true)
                        intent.putExtra(Constants.LOGIN, true)
                        intent.putExtra(Constants.API, api)
                        startActivity(intent)
                    } else {
                        //User exists and attendance too.
                        val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                        var res = response
                        res += "kkk" + param[1]
                        intent.putExtra(Constants.RESULTS, res)
                        intent.putExtra(Constants.REGISTRATION_NUMBER, user!!.text.toString())
                        intent.putExtra(Constants.LOGIN, true)
                        intent.putExtra(Constants.STUDENT_NAME, studentName)
                        intent.putExtra(Constants.API, api)
                        edit!!.putString(param[1], response)
                        edit!!.apply()
                        startActivity(intent)
                    }
                },
                Response.ErrorListener { error: VolleyError? ->
                    progressBar!!.visibility = View.INVISIBLE
                    welcomeMessage!!.visibility = View.GONE
                    login!!.visibility = View.VISIBLE
                    passLayout!!.isPasswordVisibilityToggleEnabled = true
                    if (error is AuthFailureError) {
                        val snackbar = Snackbar.make(mainLayout!!, "Wrong Credentials!", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    } else if (error is ServerError) {
                        if (Constants.offlineDataPreference!!.getString("StudentAttendance", null) == null) {
                            user!!.isEnabled = true
                            pass!!.isEnabled = true
                            user!!.isFocusableInTouchMode = true
                            user!!.isFocusable = true
                            pass!!.isFocusableInTouchMode = true
                            pass!!.isFocusable = true
                            val snackbar = Snackbar.make(mainLayout!!, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            Constants.Offlin_mode = true
                            val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is NetworkError) {
                        if (Constants.offlineDataPreference!!.getString("StudentAttendance", null) == null) {
                            user!!.isEnabled = true
                            pass!!.isEnabled = true
                            user!!.isFocusableInTouchMode = true
                            user!!.isFocusable = true
                            pass!!.isFocusableInTouchMode = true
                            pass!!.isFocusable = true
                            val snackbar = Snackbar.make(mainLayout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        } else {
                            Constants.Offlin_mode = true
                            val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is TimeoutError) {
                        if (!track) {
                            progressBar!!.visibility = View.VISIBLE
                            welcomeMessage!!.visibility = View.VISIBLE
                            login!!.visibility = View.GONE
                            user!!.isEnabled = false
                            pass!!.isEnabled = false
                            user!!.isFocusable = true
                            pass!!.isFocusable = true
                            passLayout!!.isPasswordVisibilityToggleEnabled = false
                            track = true
                            login!!.performClick()
                        } else {
                            if (Constants.offlineDataPreference!!.getString("StudentAttendance", null) == null) {
                                user!!.isEnabled = true
                                pass!!.isEnabled = true
                                user!!.isFocusableInTouchMode = true
                                user!!.isFocusable = true
                                pass!!.isFocusableInTouchMode = true
                                pass!!.isFocusable = true
                                val snackbar = Snackbar.make(mainLayout!!, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT)
                                snackbar.show()
                                track = false
                            } else {
                                Constants.Offlin_mode = true
                                val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user"] = param[1]!!
                params["pass"] = param[2]!!
                return params
            }
        }
        queue.add(postRequest)
    }

    private fun getName(vararg param: String) {
        val queue = Volley.newRequestQueue(applicationContext)
        val postRequest: StringRequest = object : StringRequest(Method.POST, param[0] + "/studentinfo",
                Response.Listener { response: String? ->
                    try {
                        val jobj = JSONObject(response.toString())
                        Log.d("response", jobj.toString())
                        val jarr = jobj.getJSONArray("detail")
                        val jobj1 = jarr.getJSONObject(0)
                        studentName = jobj1.getString("name")
                        student_branch = jobj1.getString(Constants.STUDENTBRANCH)
                        editor = preferences!!.edit()
                        Log.d("branch_portal", student_branch.toString())
                        editor!!.putString(Constants.STUDENT_NAME, studentName)
                        editor!!.putString(Constants.STUDENTBRANCH, student_branch)
                        editor!!.apply()
                        getData(api, param[1], param[2])
                    } catch (e: JSONException) {
                        Toast.makeText(this@MainActivity.applicationContext, "Cannot fetch name!!", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error: VolleyError? ->
                    progressBar!!.visibility = View.INVISIBLE
                    welcomeMessage!!.visibility = View.GONE
                    login!!.visibility = View.VISIBLE
                    passLayout!!.isPasswordVisibilityToggleEnabled = true
                    if (error is AuthFailureError) {
                        val snackBar = Snackbar.make(mainLayout!!, "Wrong Credentials!", Snackbar.LENGTH_SHORT)
                        snackBar.show()
                    } else if (error is ServerError) {
                        if (Constants.offlineDataPreference!!.getString("StudentAttendance", null) == null) {
                            user!!.isEnabled = true
                            pass!!.isEnabled = true
                            user!!.isFocusableInTouchMode = true
                            user!!.isFocusable = true
                            pass!!.isFocusableInTouchMode = true
                            pass!!.isFocusable = true
                            val snackBar = Snackbar.make(mainLayout!!, "Cannot connect to ITER servers right now. Try again with correct credentials.", Snackbar.LENGTH_SHORT)
                            snackBar.show()
                        } else {
                            Constants.Offlin_mode = true
                            val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is NetworkError) {
                        if (Constants.offlineDataPreference!!.getString("StudentAttendance", null) == null) {
                            user!!.isEnabled = true
                            pass!!.isEnabled = true
                            user!!.isFocusableInTouchMode = true
                            user!!.isFocusable = true
                            pass!!.isFocusableInTouchMode = true
                            pass!!.isFocusable = true
                            val snackBar = Snackbar.make(mainLayout!!, "Cannot establish connection", Snackbar.LENGTH_SHORT)
                            snackBar.show()
                        } else {
                            Constants.Offlin_mode = true
                            val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (error is TimeoutError) {
                        if (!track) {
                            progressBar!!.visibility = View.VISIBLE
                            welcomeMessage!!.visibility = View.VISIBLE
                            login!!.visibility = View.GONE
                            user!!.isEnabled = false
                            pass!!.isEnabled = false
                            user!!.isFocusable = true
                            pass!!.isFocusable = true
                            passLayout!!.isPasswordVisibilityToggleEnabled = false
                            track = true
                            login!!.performClick()
                        } else {
                            if (Constants.offlineDataPreference!!.getString("StudentAttendance", null) == null) {
                                user!!.isEnabled = true
                                pass!!.isEnabled = true
                                user!!.isFocusableInTouchMode = true
                                user!!.isFocusable = true
                                pass!!.isFocusableInTouchMode = true
                                pass!!.isFocusable = true
                                val snackBar = Snackbar.make(mainLayout!!, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT)
                                snackBar.show()
                                track = false
                            } else {
                                Constants.Offlin_mode = true
                                val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
                                startActivity(intent)
                            }
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

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun askPermission() {
        if (!hasPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("Please allow to access storage")
                        .setCancelable(false)
                        .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_CODE)
                            }
                        }
                        .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            finish()
                        }.create().show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_CODE)
                }
            }
        }
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "1")
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("Please allow to access storage. Press OK to enable in settings.")
                            .setCancelable(false)
                            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivityForResult(intent, EXTERNAL_STORAGE_PERMISSION_CODE)
                            }
                            .setNegativeButton("Cancel") { dialog: DialogInterface, which: Int ->
                                dialog.dismiss()
                                finish()
                            }.create().show()
                } else {
                    AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("Please allow to access storage")
                            .setCancelable(false)
                            .setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_CODE)
                                }
                            }
                            .setNegativeButton("Cancel") { dialog: DialogInterface, which: Int ->
                                dialog.dismiss()
                                finish()
                            }.create().show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val MY_REQUEST_CODE = 1011
        private const val EXTERNAL_STORAGE_PERMISSION_CODE = 1002
        fun convertDpToPixel(dp: Float): Int {
            val metrics = Resources.getSystem().displayMetrics
            val px = dp * (metrics.densityDpi / 160f)
            return px.roundToInt()
        }
    }
}