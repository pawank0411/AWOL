package codex.codex_iter.www.awol.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.adapter.AttendanceAdapter.myViewHolder
import codex.codex_iter.www.awol.model.AttendanceData

class AttendanceAdapter(private val ctx: Context, private val datalist: List<AttendanceData>?) : RecyclerView.Adapter<myViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.item_attendance, parent, false)
        return myViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val theme = ctx.getSharedPreferences("theme", 0)
        val dark = theme.getBoolean("dark_theme", false)
        holder.sub.text = datalist!![position].sub
        val p = datalist[position].percent
        var percent = 0.0
        if (p != null) {
            percent = p.toDouble()
        }
        if (percent > 80.toFloat()) {
            holder.ta.setBackgroundColor(Color.parseColor("#0BBE62"))
        } else if (percent >= 60.toFloat() && percent <= 80) {
            holder.ta.setBackgroundColor(Color.parseColor("#FFFF66"))
        } else {
            holder.ta.setBackgroundColor(Color.parseColor("#F5FC0101"))
        }
        holder.ta.text = datalist[position].percent + "%"
        val s = datalist[position].old
        if (s != "") {
            val n = datalist[position].percent!!.toDouble()
            val o = s!!.toDouble()
            //    Toast.makeText(context, "New Attendance : " + n, Toast.LENGTH_SHORT).show();
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up)
            } else {
                holder.up.setBackgroundResource(R.drawable.down)
            }
        }
        if (s != "") {
            val n = datalist[position].percent!!.toDouble()
            val o = s!!.toDouble()
            //    Toast.makeText(context, "New Attendance : " + n, Toast.LENGTH_SHORT).show();
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up)
            } else {
                holder.up.setBackgroundResource(R.drawable.down)
            }
        }
        holder.lu.text = datalist[position].upd
        holder.th.text = datalist[position].theory + datalist[position].that
        holder.prac.text = datalist[position].lab + datalist[position].labt
        holder.ab.text = datalist[position].absent
        holder.tc.text = datalist[position].classes
        holder.bunk_text.text = datalist[position].bunk_text_str
        if (!dark) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.ta.setTextColor(Color.parseColor("#141831"))
            holder.lu.setTextColor(Color.parseColor("#141831"))
            holder.th.setTextColor(Color.parseColor("#141831"))
            holder.prac.setTextColor(Color.parseColor("#141831"))
            holder.ab.setTextColor(Color.parseColor("#141831"))
            holder.tc.setTextColor(Color.parseColor("#141831"))
            // mViewHolder.total.setTextColor(Color.parseColor("#141831"));
            holder.updated.setTextColor(Color.parseColor("#141831"))
            holder.absents.setTextColor(Color.parseColor("#141831"))
            holder.pract.setTextColor(Color.parseColor("#141831"))
            holder.theory.setTextColor(Color.parseColor("#141831"))
            holder.classes.setTextColor(Color.parseColor("#141831"))
            holder.bunk_text.setTextColor(Color.parseColor("#141831"))
        }
    }

    override fun getItemCount(): Int {
        return datalist!!.size
    }

    inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var sub: TextView
        var lu: TextView
        var th: TextView
        var prac: TextView
        var ab: TextView
        var tc: TextView
        var theory: TextView
        var updated: TextView
        var pract: TextView
        var classes: TextView
        var absents: TextView
        var bunk_text: TextView
        var ta: Button
        var up: ImageView
        var cardView: CardView

        init {

//            total = view.findViewById(R.id.total);
            theory = view.findViewById(R.id.theory_t)
            updated = view.findViewById(R.id.updated)
            pract = view.findViewById(R.id.practicle)
            classes = view.findViewById(R.id.classes)
            absents = view.findViewById(R.id.absents)
            cardView = view.findViewById(R.id.card_view)
            sub = view.findViewById(R.id.sub)
            lu = view.findViewById(R.id.lu)
            th = view.findViewById(R.id.theory)
            prac = view.findViewById(R.id.prac)
            ab = view.findViewById(R.id.ab)
            tc = view.findViewById(R.id.tc)
            ta = view.findViewById(R.id.ta)
            bunk_text = view.findViewById(R.id.bunk_text)

//            tha=view.findViewById(R.id.tha);
//            la=view.findViewById(R.id.la);
            up = view.findViewById(R.id.up)
            //down=view.findViewById(R.id.down);
        }
    }

}