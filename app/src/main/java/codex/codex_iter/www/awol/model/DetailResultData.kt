package codex.codex_iter.www.awol.model

class DetailResultData {
    var subjectDesc: String? = null
    var styNumber: String? = null
    var subjectCode: String? = null
    var grade: String? = null
    var earnedCredit: String? = null

    companion object {
        lateinit var detailResultData: Array<DetailResultData?>
    }
}