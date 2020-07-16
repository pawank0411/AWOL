package codex.codex_iter.www.awol.model

class ResultData {
    var styNumber = 0
    var fail: String? = null
    var semesterDesc: String? = null
    var totalearnedCredit: String? = null
    var holdProcessing: String? = null
    var examperiodFrom: String? = null
    var sgpaR: String? = null
    var cgpaR: String? = null

    companion object {
        lateinit var ld: Array<ResultData?>
    }
}