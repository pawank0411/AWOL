package codex.codex_iter.www.awol.application

import android.app.Application
import java.io.File
import java.util.*

class CleanCacheApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun clearApplicationData() {
        val cacheDirectory = cacheDir
        val applicationDirectory = File(Objects.requireNonNull(cacheDirectory.parent))
        if (applicationDirectory.exists()) {
            val fileNames = applicationDirectory.list()
            if (fileNames != null) {
                for (fileName in fileNames) {
                    if (fileName != "lib") {
                        deleteFile(File(applicationDirectory, fileName))
                    }
                }
            }
        }
    }

    companion object {
        var instance: CleanCacheApplication? = null
            private set

        fun deleteFile(file: File?): Boolean {
            var deletedAll = true
            if (file != null) {
                if (file.isDirectory) {
                    val children = file.list()
                    if (children != null) {
                        for (child in children) {
                            deletedAll = deleteFile(File(file, child)) && deletedAll
                        }
                    }
                } else {
                    deletedAll = file.delete()
                }
            }
            return deletedAll
        }
    }
}