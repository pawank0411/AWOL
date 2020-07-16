package codex.codex_iter.www.awol.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.theme.ThemeAdapter.ThemeViewHolder

class ThemeAdapter(var themes: List<ThemeItem?>?, var selectedPosition: Int, private val isBackgroundDark: Boolean) : RecyclerView.Adapter<ThemeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.theme_item_layout, parent, false)
        return ThemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        if (position == selectedPosition) {
            holder.selector.visibility = View.VISIBLE
        } else {
            holder.selector.visibility = View.GONE
        }
        val item = themes!![position]
        holder.preview.setColor(item!!.mainColor, item!!.isDark, isBackgroundDark)
        holder.itemView.setOnClickListener {
            val pos = selectedPosition
            selectedPosition = position
            notifyItemChanged(pos)
            holder.selector.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return themes!!.size
    }

    inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var preview: ThemeDrawable
        var selector: ImageView

        init {
            preview = itemView.findViewById(R.id.preview)
            selector = itemView.findViewById(R.id.checked)
        }
    }

}