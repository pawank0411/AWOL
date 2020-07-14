package codex.codex_iter.www.awol.utilities

import android.app.ProgressDialog
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.text.Html
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class DownloadScrapFile(private val context: Context) {
    private var progressDialog: ProgressDialog? = null

    // DownloadTask for downloding video from URL
    inner class DownloadTask internal constructor(private val context: Context, private val file: String, private val fromMainActivity: Boolean, private val message: String?) : AsyncTask<String?, Int?, String?>() {
        var fileN: String? = null
        protected override fun doInBackground(vararg sUrl: String): String? {
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val url = URL(sUrl[0])
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return ("Server returned HTTP " + connection.responseCode
                            + " " + connection.responseMessage)
                }
                val fileLength = connection.contentLength
                input = connection.inputStream
                if (!fromMainActivity) {
                    fileN = "$file.txt"
                    val filename = File(context.filesDir.toString() + "/", fileN)
                    output = FileOutputStream(filename)
                } else {
                    fileN = "$file.apk"
                    val filename = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileN)
                    output = FileOutputStream(filename)
                }
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    if (isCancelled) {
                        input.close()
                        return null
                    }
                    total += count.toLong()
                    if (fileLength > 0) publishProgress((total * 100 / fileLength).toInt())
                    output.write(data, 0, count)
                }
            } catch (e: Exception) {
                return e.toString()
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (ignored: IOException) {
                }
                connection?.disconnect()
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            if (fromMainActivity) {
                progressDialog = ProgressDialog(context)
                progressDialog!!.setTitle(Html.fromHtml("<font color='black'>Downloading new updates</font>"))
                progressDialog!!.setMessage(Html.fromHtml("<font color='black'>Please wait...</font>"))
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                Log.d("DownloadError", result)
            } else {
                if (fromMainActivity) {
                    progressDialog!!.dismiss()
                    Utils.updateAvailable(context, message)
                }
            }
            MediaScannerConnection.scanFile(context, arrayOf(context.filesDir.toString() + "/", fileN), null
            ) { newpath: String, newuri: Uri ->
                Log.i("ExternalStorage", "Scanned $newpath:")
                Log.i("ExternalStorage", "-> uri=$newuri")
            }
            MediaScannerConnection.scanFile(context, arrayOf(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), fileN), null
            ) { newpath: String, newuri: Uri ->
                Log.i("ExternalStorage", "Scanned $newpath:")
                Log.i("ExternalStorage", "-> uri=$newuri")
            }
        }

    }

    //hare you can start downloding video
    fun newDownload(url: String?, filename: String, fromMainActivity: Boolean, message: String?) {
        val downloadTask = DownloadTask(context, filename, fromMainActivity, message)
        downloadTask.execute(url)
    }

}