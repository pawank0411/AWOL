package codex.codex_iter.www.awol.model

class Lecture {
    var sem: String? = null
    var subject: String? = null
    var name: String? = null
    var link: String? = null

    constructor(name: String?, link: String?) {
        this.name = name
        this.link = link
    }

    constructor(subject: String?) {
        this.subject = subject
    }

}