package codex.codex_iter.www.awol.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.activity.BunkActivity
import codex.codex_iter.www.awol.model.AttendanceData
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.math.roundToInt

class BunkActivity : BaseThemedActivity() {
    var atndedt: EditText? = null
    var bnkedt: EditText? = null
    private var taredt: EditText? = null
    var result: TextView? = null
    var left: TextView? = null
    var sub: TextView? = null
    private var target_atd: TextView? = null
    private var classes_bunk: TextView? = null
    private var going_attend: TextView? = null
    private var target: Button? = null
    private var bunk: Button? = null
    private var attend: Button? = null
    var absent = 0.0
    var total = 0.0
    var percent = 0.0
    var present = 0.0
    var ld: Array<AttendanceData?>? = null

    inner class DogsDropdownOnItemClickListener : AdapterView.OnItemClickListener {
        var TAG = "DogsDropdownOnItemClickListener.java"
        override fun onItemClick(arg0: AdapterView<*>?, v: View, arg2: Int, arg3: Long) {
            val fadeInAnimation = AnimationUtils.loadAnimation(v.context, android.R.anim.fade_in)
            fadeInAnimation.duration = 10
            v.startAnimation(fadeInAnimation)
            popupWindowDogs!!.dismiss()
            val selectedItemText = (v as TextView).text.toString()
            sub!!.text = selectedItemText
            total = ld!![arg2]!!.getClasses().toString().toDouble()
            absent = ld!![arg2]!!.getAbsent().toDouble()
            percent = ld!![arg2]!!.percent!!.toDouble()
            present = total - absent
            if (75 < percent) {
                var i = 0
                while (i != -99) {
                    val p = present / (total + i) * 100
                    if (p < 75) break
                    i++
                }
                if (i > 1) {
                    result!!.text = "Bunk " + (i - 1) + " classes for 75% "
                } else {
                    result!!.text = " "
                }
            } else if (75 > percent) {
                var i = 0
                while (i != -99) {
                    val p = (present + i) / (total + i) * 100
                    if (p > 75) break
                    i++
                }
                if (i > 1) {
                    result!!.text = "Attend " + (i - 1) + " classes for 75%"
                } else {
                    result!!.text = " "
                }
            }
            left!!.text = ""
        }
    }

    private fun popupWindowDogs(subn: Array<String?>): PopupWindow {

        // initialize a pop up window type
        val popupWindow = PopupWindow(this)

        // the drop down list is a list view
        val listViewDogs = ListView(this)

        // set our adapter and pass our pop up window contents
        listViewDogs.adapter = dogsAdapter(subn)

        // set the item click listener
        listViewDogs.onItemClickListener = DogsDropdownOnItemClickListener()

        // some other visual settings
        popupWindow.isFocusable = true
        popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT

        // set the list view as pop up window content
        popupWindow.contentView = listViewDogs
        return popupWindow
    }

    private fun dogsAdapter(dogsArray: Array<String?>): ArrayAdapter<String?> {
        return object : ArrayAdapter<String?>(this@BunkActivity, android.R.layout.simple_list_item_1, dogsArray) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val item = getItem(position)
                val listItem = TextView(this@BunkActivity)
                listItem.text = item
                listItem.tag = position
                listItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                if (!dark) {
                    listItem.setBackgroundColor(Color.WHITE)
                    listItem.setTextColor(Color.BLACK)
                } else {
                    listItem.setBackgroundColor(Color.parseColor("#141414"))
                    listItem.setTextColor(Color.WHITE)
                }
                val padding = (resources.displayMetrics.density * 16).roundToInt()
                listItem.setPadding(padding, padding, padding, padding)
                return listItem
            }
        }
    }

    var popupWindowDogs: PopupWindow? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bunk)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Plan a bunk"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
            setDisplayShowHomeEnabled(true)
        }
        if (!dark) {
           myDrawableCompact()
        }
        ld = AttendanceData.attendanceData
        if (ld != null) {
            val subn = arrayOfNulls<String>(ld!!.size)
            for (i in ld!!.indices) subn[i] = ld!![i]!!.getSubject().toString()
            popupWindowDogs = popupWindowDogs(subn)
        } else {
            val userm = getSharedPreferences("user",
                    Context.MODE_PRIVATE)
            val u = userm.getString("user", "")
            val p = userm.getString("pass", "")
            Crashlytics.log(Log.ERROR, "error1_u", u)
            Crashlytics.log(Log.ERROR, "error2_p", p)
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
        val view1 = findViewById<View>(R.id.view1)
        val view2 = findViewById<View>(R.id.view2)
        if (dark) {
            view1.setBackgroundColor(Color.parseColor("#A9A9A9"))
            view2.setBackgroundColor(Color.parseColor("#A9A9A9"))
        }
        sub = findViewById(R.id.sub)
        target_atd = findViewById(R.id.target_at)
        classes_bunk = findViewById(R.id.classes_bunk)
        going_attend = findViewById(R.id.going_attend)
        if (dark) {
            sub!!.setTextColor(Color.WHITE)
            target_atd!!.setTextColor(Color.WHITE)
            classes_bunk!!.setTextColor(Color.WHITE)
            going_attend!!.setTextColor(Color.WHITE)
        }
        sub!!.setOnClickListener { view: View? -> popupWindowDogs!!.showAsDropDown(view, 0, (resources.displayMetrics.density * 16).roundToInt()) }
        present = total - absent
        atndedt = findViewById(R.id.atndedt)
        bnkedt = findViewById(R.id.bnkedt)
        taredt = findViewById(R.id.taredt)
        attend = findViewById(R.id.attend)
        bunk = findViewById(R.id.bunk)
        target = findViewById(R.id.target)
        result = findViewById(R.id.result)
        left = findViewById(R.id.left)
        target!!.setOnClickListener {
            val s = taredt!!.text.toString().trim { it <= ' ' }
            var s_t = 0
            if (s != "") {
                s_t = s.toInt()
            }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(taredt!!.windowToken, 0)
            if (s == "0" || s_t > 100 || s == "00" || s == "000") {
                Snackbar.make(findViewById(R.id.ll), "Enter Valid Value", Snackbar.LENGTH_SHORT).show()
            } else if (s == "") Snackbar.make(findViewById(R.id.ll), "Enter Some Value", Snackbar.LENGTH_SHORT).show() else if (s == "100" && absent > 0) Snackbar.make(findViewById(R.id.ll), "Not Possible!!", Snackbar.LENGTH_SHORT).show() else {
                val tp = Scanner(s).nextDouble()
                if (tp < percent) {
                    var i: Int
                    var p: Double
                    i = 0
                    while (i != -99) {
                        p = present / (total + i) * 100
                        if (p < tp) break
                        i++
                    }
                    if (i > 1000) {
                        result!!.text = "Don't need to attend the classes."
                    } else {
                        result!!.text = "Bunk " + (i - 1) + " classes for req attendance"
                        if (tp.toInt() != 75) {
                            val bunk = i - 1
                            if (75 < tp) {
                                i = 0
                                while (i != 99) {
                                    p = present / (total + bunk + i) * 100
                                    if (p < 75) break
                                    i++
                                }
                                left!!.text = "Bunk " + (i - 1) + " more classes for 75% "
                            } else if (75 > tp) {
                                i = 0
                                while (i != -99) {
                                    p = (present + i) / (total + bunk + i) * 100
                                    if (p > 75) break
                                    i++
                                }
                                left!!.text = "Attend " + (i - 1) + " classes after bunk for 75%"
                            }
                        } else left!!.text = ""
                    }
                } else if (tp > percent) {
                    var i: Int
                    i = 0
                    while (i != -99) {
                        val p = (present + i) / (total + i) * 100
                        if (p > tp) break
                        i++
                    }
                    if (i > 1000) {
                        result!!.text = "Don't need to attend the classes."
                    } else {
                        result!!.text = "Attend $i classes for req attendance"
                        if (tp.toInt() != 75) {
                            val attend = i.toDouble()
                            var p: Double
                            if (75 < tp) {
                                i = 0
                                while (i != -99) {
                                    p = (present + attend) / (total + attend + i) * 100
                                    if (p < 75) break
                                    i++
                                }
                                left!!.text = "Bunk " + (i - 1) + " classes afterwards for 75% "
                            } else if (75 > tp) {
                                i = 0
                                while (i != -99) {
                                    p = (present + attend + i) / (total + attend + i) * 100
                                    if (p > 75) break
                                    i++
                                }
                                left!!.text = "Attend " + (i - 1) + " more classes for 75%"
                            }
                        }
                    }
                }
            }
        }
        attend!!.setOnClickListener {
            val s = atndedt!!.text.toString().trim { it <= ' ' }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(atndedt!!.windowToken, 0)
            if (s == "") Snackbar.make(findViewById(R.id.ll), "Enter Some Value", Snackbar.LENGTH_SHORT).show() else {
                val c = Scanner(s).nextInt()
                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (currentFocus != null) inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                val p = (present + c) / (total + c) * 100
                if (p > 0) {
                    result!!.text = "Your attendance will be " + String.format(Locale.US, "%.2f", p) + "%"
                    var i: Int
                    var pr: Double
                    if (75 < p) {
                        i = 0
                        while (i != -99) {
                            pr = (present + c) / (total + c + i) * 100
                            if (pr < 75) break
                            i++
                        }
                        left!!.text = "Bunk " + (i - 1) + " classes afterwards for 75% "
                    } else if (75 > p) {
                        i = 0
                        while (i != -99) {
                            pr = (present + c + i) / (total + c + i) * 100
                            if (pr > 75) break
                            i++
                        }
                        left!!.text = "Attend " + (i - 1) + " more classes for 75%"
                    }
                } else {
                    Snackbar.make(findViewById(R.id.ll), "Enter Valid value", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        bunk!!.setOnClickListener {
            val s = bnkedt!!.text.toString().trim { it <= ' ' }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(bnkedt!!.windowToken, 0)
            if (s == "") Snackbar.make(findViewById(R.id.ll), "Enter Some value", Snackbar.LENGTH_SHORT).show() else {
                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (currentFocus != null) inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                val c = Scanner(bnkedt!!.text.toString().trim { it <= ' ' }).nextInt()
                val p = present / (total + c) * 100
                if (p > 0) {
                    result!!.text = "Your attendance will be " + String.format(Locale.US, "%.2f", p) + "%"
                    var i: Int
                    var pr: Double
                    if (75 < p) {
                        i = 0
                        while (i != -99) {
                            pr = present / (total + c + i) * 100
                            if (pr < 75) break
                            i++
                        }
                        left!!.text = "Bunk " + (i - 1) + " more classes for 75% "
                    } else if (75 > p) {
                        i = 0
                        while (i != -99) {
                            pr = (present + i) / (total + c + i) * 100
                            if (pr > 75) break
                            i++
                        }
                        left!!.text = "Attend " + (i - 1) + " classes after bunk for 75%"
                    }
                } else {
                    Snackbar.make(findViewById(R.id.ll), "Enter Valid Value", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        bnkedt!!.setOnTouchListener { _: View?, _: MotionEvent? ->
            taredt!!.setText("")
            atndedt!!.setText("")
            result!!.text = ""
            left!!.text = ""
            false
        }
        atndedt!!.setOnTouchListener { v: View?, event: MotionEvent? ->
            bnkedt!!.setText("")
            taredt!!.setText("")
            result!!.text = ""
            left!!.text = ""
            false
        }
        taredt!!.setOnTouchListener { v: View?, event: MotionEvent? ->
            bnkedt!!.setText("")
            atndedt!!.setText("")
            result!!.text = ""
            left!!.text = ""
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
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
}