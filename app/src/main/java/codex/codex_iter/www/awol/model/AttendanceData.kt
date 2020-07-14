package codex.codex_iter.www.awol.model

import java.util.*

class AttendanceData {
    var sub: String? = null
    var code: String? = null
    var upd: String? = null
    var theory: String? = null
        private set
    var lab: String? = null
        private set
    private var percent: String? = null
    var that = ""
    var labt = ""
    var old = ""
    var bunk_text_str = "Don't Bunk Anymore"
        private set
    private var thT = 0.0
    private var thp = 0.0
    private var lat = 0.0
    private var lap = 0.0
    val classes: String
        get() = Integer.toString((thT + lat).toInt())

    val status: Int
        get() {
            val d = Scanner(percent).nextDouble()
            return if (d < 65) 1 else if (d >= 65 && d < 75) 2 else if (d >= 75 && d < 90) 3 else 4
        }

    fun setTheory(theory: String) {
        if (theory == "Not Applicable") {
            this.theory = theory
            thp = 0.0
            thT = 0.0
        } else {
            val `in` = Scanner(theory)
            thp = `in`.nextInt().toDouble()
            val c = `in`.next()[0]
            thT = `in`.nextInt().toDouble()
            val res = " (" + String.format(Locale.US, "%.0f", thp / thT * 100) + "%)"
            this.theory = theory
            that = res
        }
    }

    fun setLab(lab: String) {
        if (lab == "Not Applicable") {
            this.lab = lab
            lap = 0.0
            lat = 0.0
        } else {
            val `in` = Scanner(lab)
            lap = `in`.nextInt().toDouble()
            val c = `in`.next()[0]
            lat = `in`.nextInt().toDouble()
            this.lab = lab
            labt = " (" + String.format(Locale.US, "%.0f", lap / lat * 100) + "%)"
        }
    }

    val absent: String
        get() {
            val i = Math.floor(lat + thT - thp - lap).toInt()
            return Integer.toString(i)
        }

    fun getPercent(): String? {
        return percent
    }

    fun setPercent(percent: String?) {
        this.percent = String.format(Locale.US, "%.1f", Scanner(percent).nextDouble())
    }

    fun setBunk() {
        val percent_class = getPercent()!!.toDouble()
        val total_class = classes.toDouble()
        val absent = absent.toDouble()
        val present = total_class - absent
        var i: Int
        if (percent_class >= 75) {
            //to be continued...
            i = 0
            while (i != -99) {
                val p = present / (total_class + i) * 100
                if (p < 75) break
                i++
            }
            i--
            if (i > 0) {
                if (i != 1) {
                    bunk_text_str = "BUNK $i classes for 75%"
                } else {
                    bunk_text_str = "BUNK $i class for 75%"
                }
            }
        } else {
            i = 0
            while (i != -99) {
                val p = (present + i) / (total_class + i) * 100
                if (p > 75) break
                i++
            }
            i--
            if (i > 0) {
                if (i != 1) {
                    bunk_text_str = "Attend $i classes for 75%"
                } else {
                    bunk_text_str = "Attend $i class for 75%"
                }
            }
        }
    }

    companion object {
        var attendanceData: Array<AttendanceData?>
    }
}