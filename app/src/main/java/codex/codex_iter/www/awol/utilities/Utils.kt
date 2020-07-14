package codex.codex_iter.www.awol.utilities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object Utils {
    fun getJsonFromStorage(context: Context, fileName: String?): String {
        var jsonString: String?
        return try {
            val fis = context.openFileInput(fileName)
            val isr = InputStreamReader(fis, StandardCharsets.UTF_8)
            val bufferedReader = BufferedReader(isr)
            val sb = StringBuilder()
            while (bufferedReader.readLine().also { jsonString = it } != null) {
                sb.append(jsonString)
            }
            sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            e.toString()
        }
    }

    fun updateAvailable(context: Context, message: String?) {
        val sharedPreferences = context.getSharedPreferences("MESSAGE", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("MESSAGE", message).apply()
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "awol.apk")
        Log.d("path", file.path)
        if (file.exists()) {
            AlertDialog.Builder(context)
                    .setTitle(Html.fromHtml("<font color='black'>Update Available</font>"))
                    .setMessage("""
    What's new :
    ${sharedPreferences.getString("MESSAGE", "Bug fix")}
    """.trimIndent())
                    .setCancelable(false)
                    .setPositiveButton("UPDATE NOW") { dialog: DialogInterface?, which: Int ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(FileProvider.getUriForFile(context, context.applicationContext.packageName + ".my.package.name.provider", file),
                                "application/vnd.android.package-archive")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(intent)
                    }.setNegativeButton("LATER") { dialog: DialogInterface, which: Int -> dialog.dismiss() }.show()
        } else {
            Toast.makeText(context, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show()
        }
    }
}