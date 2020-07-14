package codex.codex_iter.www.awol.theme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import codex.codex_iter.www.awol.R

class ThemeDrawable @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var primaryColor = 0
    private var background = 0
    private var isDark = false
    private var isBackgroundDark = false
    var path1: Path
    var path2: Path
    var fill: Paint
    var lightBackground: Int
    var darkBackground: Int
    var strokeWidth: Int
    var border: Rect
    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas);
        fill.style = Paint.Style.FILL
        fill.color = primaryColor

        // canvas.drawRect(new Rect(0,0,canvas.getWidth(),canvas.getHeight()),fill);
        canvas.drawPath(path1, fill)
        fill.color = background
        canvas.drawPath(path2, fill)
        fill.style = Paint.Style.STROKE
        fill.strokeWidth = strokeWidth.toFloat()
        fill.color = if (isBackgroundDark) lightBackground else darkBackground
        canvas.drawRect(border, fill)
    }

    fun setColor(primaryColor: Int, isDark: Boolean, isBackgroundDark: Boolean) {
        //  Log.d("SETCOLOR", "setColor: "+(primaryColor));
        this.primaryColor = resources.getColor(primaryColor)
        background = if (isDark) darkBackground else lightBackground
        this.isDark = isDark
        this.isBackgroundDark = isBackgroundDark
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("WIDTH", "onSizeChanged: $w     $h")
        val threeFourthHeight = Math.round(h / 2.toFloat())
        val threeFourthWidth = Math.round(w / 2.toFloat())
        Log.d("WIDTH", "onSizeChanged: $threeFourthHeight     $threeFourthWidth")
        val halfStrokeWidth = Math.round(strokeWidth / 2.toFloat())
        border.left = halfStrokeWidth
        border.top = halfStrokeWidth
        border.right = w - halfStrokeWidth
        border.bottom = h - halfStrokeWidth
        path1.moveTo(0f, 0f)
        path1.lineTo(w.toFloat(), 0f)
        path1.lineTo(w.toFloat(), threeFourthHeight.toFloat())
        path1.lineTo(threeFourthWidth.toFloat(), h.toFloat())
        path1.lineTo(0f, h.toFloat())
        path1.close()
        path2.moveTo(threeFourthWidth.toFloat(), h.toFloat())
        path2.lineTo(w.toFloat(), h.toFloat())
        path2.lineTo(w.toFloat(), threeFourthHeight.toFloat())
        path2.close()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    init {
        path1 = Path()
        path2 = Path()
        fill = Paint()
        fill.style = Paint.Style.FILL
        lightBackground = resources.getColor(R.color.lightBackground)
        darkBackground = resources.getColor(R.color.darkBackground)
        border = Rect(0, 0, 0, 0)
        strokeWidth = Math.round(resources.displayMetrics.density * 1)
    }
}