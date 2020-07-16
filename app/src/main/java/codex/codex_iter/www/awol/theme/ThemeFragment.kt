package codex.codex_iter.www.awol.theme

import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.utilities.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThemeFragment : BottomSheetDialogFragment() {
    private var isDark = false
    private var preferences: SharedPreferences? = null
    private var items: List<ThemeItem?>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.theme_select_layout, container, false)
        items = Constants.themes
        preferences = requireActivity().getSharedPreferences("theme", 0)
        isDark = preferences!!.getBoolean(PREF_DARK_THEME, false)
        (view.findViewById<View>(R.id.title) as TextView).setTextColor(if (isDark) Color.WHITE else Color.BLACK)
        val recyclerView: RecyclerView = view.findViewById(R.id.theme_list)
        val selectedPosition: Int = if (activity != null) preferences!!.getInt(POSITION, 0) else 0
        val adapter = ThemeAdapter(items, selectedPosition, isDark)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        val apply = view.findViewById<Button>(R.id.apply_btn)
        apply.setOnClickListener { view1: View? ->
            val editor = preferences!!.edit()
            val item = items!![adapter.selectedPosition]
            editor.putInt(POSITION, adapter.selectedPosition)
            editor.putInt(THEME, item!!.theme)
            editor.putBoolean(PREF_DARK_THEME, item.isDark)
            editor.apply()
            if (activity != null) requireActivity().recreate()
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialog1: DialogInterface ->
            val d = dialog1 as BottomSheetDialog
            val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            var drawable: Drawable? = null
            if (bottomSheet != null) {
                drawable = bottomSheet.context.resources.getDrawable(R.drawable.theme_picker_bg)
            }
            var bgColor = 0
            if (bottomSheet != null) {
                bgColor = if (isDark) bottomSheet.resources.getColor(R.color.darkBackground) else Color.WHITE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, bgColor)
                }
            } else {
                drawable?.mutate()?.setColorFilter(bgColor, PorterDuff.Mode.SRC_IN)
            }
            if (bottomSheet != null) {
                bottomSheet.background = drawable
            }
            var bottomSheetBehavior: BottomSheetBehavior<*>? = null
            if (bottomSheet != null) {
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            }
            bottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(view: View, i: Int) {
                    if (i == 5) {
                        d.cancel()
                    }
                }

                override fun onSlide(view: View, v: Float) {}
            })
        }
        return dialog
    }

    companion object {
        private const val POSITION = "position"
        private const val PREF_DARK_THEME = "dark_theme"
        private const val THEME = "theme_pref"
        fun newInstance(): ThemeFragment {
            return ThemeFragment()
        }
    }
}