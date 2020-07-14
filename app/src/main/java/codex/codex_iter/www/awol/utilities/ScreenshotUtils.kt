package codex.codex_iter.www.awol.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.collection.LruCache
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

object ScreenshotUtils {
    /*  Method which will return Bitmap after taking screenshot. We have to pass the view which we want to take screenshot.  */
    fun getScreenShot(view: View?): Bitmap? {
        val recyclerView = view as RecyclerView?
        val adapter = recyclerView!!.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache = LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view!!.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmaCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }
            bigBitmap = Bitmap.createBitmap(view!!.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)
            for (i in 0 until size) {
                val bitmap = bitmaCache[i.toString()]
                if (bitmap != null) {
                    bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                }
                if (bitmap != null) {
                    iHeight += bitmap.height
                }
                bitmap?.recycle()
            }
        }
        return bigBitmap
    }

    /*  Create Directory where screenshot will save for sharing screenshot  */
    fun getMainDirectoryName(context: Context): File {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
        val mainDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Demo")

        //If File is not present create directory
        if (!mainDir.exists()) {
            if (mainDir.mkdir()) Log.e("Create Directory", "Main Directory Created : $mainDir")
        }
        return mainDir
    }

    /*  Store taken screenshot into above created path  */
    fun store(bm: Bitmap, fileName: String?, saveFilePath: File?): File {
        val dir = File(saveFilePath!!.absolutePath)
        if (!dir.exists()) if (!dir.mkdir()) {
            Log.e("msg", "Can't be created")
        } else {
            Log.e("msg", "Created")
        }
        val file = File(saveFilePath.absolutePath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }
}