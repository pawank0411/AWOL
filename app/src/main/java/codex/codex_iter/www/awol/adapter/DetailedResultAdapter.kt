package codex.codex_iter.www.awol.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.adapter.DetailedResultAdapter.DetailViewHolder
import codex.codex_iter.www.awol.model.DetailResultData

class DetailedResultAdapter(private val ctx: Context, private val detailResultData: List<DetailResultData>?) : RecyclerView.Adapter<DetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.item_detailresults, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val theme = ctx.getSharedPreferences("theme", 0)
        val dark = theme.getBoolean("dark_theme", false)
        holder.textViewSubName.text = detailResultData!![position].subjectdesc
        holder.textViewSubCode.text = detailResultData[position].subjectcode
        holder.textViewGrade.text = detailResultData[position].grade
        holder.textViewIndvCredits.text = detailResultData[position].earnedcredit
        if (!dark) {
            holder.textViewSubCode.setTextColor(Color.parseColor("#141831"))
            holder.textViewGrade.setTextColor(Color.parseColor("#141831"))
            holder.textViewIndvCredits.setTextColor(Color.parseColor("#141831"))
            holder.textViewSubName.setTextColor(Color.parseColor("#141831"))
        }
    }

    override fun getItemCount(): Int {
        return detailResultData!!.size
    }

    inner class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        CardView cardView;
        var textViewSubName: TextView
        var textViewSubCode: TextView
        var textViewGrade: TextView
        var textViewIndvCredits: TextView

        init {

//            cardView = itemView.findViewById(R.id.card_view);
            textViewSubName = itemView.findViewById(R.id.textViewSubName)
            textViewSubCode = itemView.findViewById(R.id.textViewSubCode)
            textViewGrade = itemView.findViewById(R.id.textViewGrade)
            textViewIndvCredits = itemView.findViewById(R.id.textViewIndvCredits)
        }
    }

}