package codex.codex_iter.www.awol.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.model.ResultData

class ResultAdapter(private val ctx: Context, private val resultData: List<ResultData>?, private val listener: OnItemClickListener?) : RecyclerView.Adapter<ResultAdapter.ResultHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultHolder {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.item_results, parent, false)
        return ResultHolder(view)
    }

    override fun onBindViewHolder(holder: ResultHolder, position: Int) {
        val theme = ctx.getSharedPreferences("theme", 0)
        val dark = theme.getBoolean("dark_theme", false)
        holder.semTextView.text = resultData!![position].styNumber.toString()
        holder.creditsTextView.text = resultData[position].totalearnedCredit
        holder.sgpaTextView.text = resultData[position].sgpaR
        holder.cgpaTextView.text = resultData[position].cgpaR
        val currSGPA = resultData[position].sgpaR.toString().toDouble()
        if (currSGPA in 8.5..10.0) {
            //excellent
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_excellent)
        } else if (currSGPA >= 7.0 && currSGPA < 8.5) {
            //good
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_good)
        } else if (currSGPA >= 5.0 && currSGPA < 7.0) {
            //average
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_average)
        } else {
            //poor
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_poor)
        }
        holder.cardView.setOnClickListener {
            listener?.onResultClicked(resultData[position].styNumber, resultData[position].totalearnedCredit,
                    resultData[position].fail, resultData[position].sgpaR)
        }
        if (!dark) {
            holder.semTextView.setTextColor(Color.parseColor("#141831"))
            holder.sgpaTextView.setTextColor(Color.parseColor("#141831"))
            holder.cgpaTextView.setTextColor(Color.parseColor("#141831"))
            holder.creditsTextView.setTextColor(Color.parseColor("#141831"))
        }
    }

    override fun getItemCount(): Int {
        return resultData!!.size
    }

    class ResultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var semTextView: TextView
        var sgpaTextView: TextView
        var creditsTextView: TextView
        var cgpaTextView: TextView
        var recyclerResultParent: LinearLayout
        var imageViewResultEmotion: ImageView
        var cardView: CardView

        init {
            cardView = itemView.findViewById(R.id.card_view)
            recyclerResultParent = itemView.findViewById(R.id.recyclerResultParent)
            semTextView = itemView.findViewById(R.id.textViewSem)
            creditsTextView = itemView.findViewById(R.id.textViewCredits)
            sgpaTextView = itemView.findViewById(R.id.textViewSGPA)
            cgpaTextView = itemView.findViewById(R.id.textViewCGPA)
            imageViewResultEmotion = itemView.findViewById(R.id.imageViewResultEmotion)
        }
    }

    interface OnItemClickListener {
        fun onResultClicked(sem: Int, totalCredits: String?, status: String?, sgpa: String?)
    }

}