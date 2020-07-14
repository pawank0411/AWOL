package codex.codex_iter.www.awol.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.model.Lecture
import com.google.android.material.textview.MaterialTextView
import java.util.*

class OnlineLectureSubjectAdapter(private val mcontext: Context, private val subjectArrayList: ArrayList<Lecture>, private val fromSubject: Boolean, private val onItemClickListener: OnItemClickListener?) : RecyclerView.Adapter<OnlineLectureSubjectAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mcontext)
        val view = inflater.inflate(R.layout.item_lectures, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = mcontext.getSharedPreferences("theme", 0)
        val dark = theme.getBoolean("dark_theme", false)
        if (dark) {
            holder.subject.setTextColor(Color.parseColor("#ffffff"))
            if (!fromSubject) {
                holder.subject.text = subjectArrayList[position].subject
                holder.icon.setImageResource(R.drawable.folder_dark)
            } else {
                holder.subject.text = subjectArrayList[position].name
                holder.icon.setImageResource(R.drawable.file_outline_dark)
            }
        } else {
            if (!fromSubject) {
                holder.subject.text = subjectArrayList[position].subject
                holder.icon.setImageResource(R.drawable.folder_light)
            } else {
                holder.subject.text = subjectArrayList[position].name
                holder.icon.setImageResource(R.drawable.file_outline_light)
            }
        }
        holder.cardView.setOnClickListener { view: View? ->
            onItemClickListener?.onClicked(subjectArrayList[position].subject, subjectArrayList[position].link)
        }
    }

    override fun getItemCount(): Int {
        return subjectArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subject: MaterialTextView
        var cardView: CardView
        var icon: ImageView

        init {
            cardView = itemView.findViewById(R.id.card_view)
            subject = itemView.findViewById(R.id.subject_name)
            icon = itemView.findViewById(R.id.icon)
        }
    }

    interface OnItemClickListener {
        fun onClicked(subject_name: String?, video_link: String?)
    }

}