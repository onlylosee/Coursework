package com.onlylose.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class DataClass(var ftime: String?, var firstLessonName : String, var firstLessonTeacher : String?, var stime: String?, var firstLessonCabinet : String?, var subgroup : Int?)



class MyAdapter(private val context: Context, private val newList: ArrayList<DataClass>) : RecyclerView.Adapter<MyAdapter.MyViewHolder> () {
    private lateinit var a : ScheduleFragment
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = newList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var sharedPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val currentItem = newList[position]
        if (currentItem.firstLessonName.isEmpty()){
            if (sharedPrefs.getBoolean("switchNameOfLess", false)) holder.tvLesson.text = "Нет пары"
            else holder.tvLesson.text = ""
            holder.tvTeacher.text = ""
            holder.tvCabinet.text = ""
            holder.tvSubgroup.text = ""
        }
        else{
            holder.tvLesson.text = currentItem.firstLessonName
            holder.tvTeacher.text = currentItem.firstLessonTeacher
            holder.tvCabinet.text = currentItem.firstLessonCabinet
            if (currentItem.subgroup == 0){
                holder.tvSubgroup.text = "Подгруппа общая"
            }
            else{
                holder.tvSubgroup.text = "Подгруппа " + currentItem.subgroup
            }
        }
        holder.tvTime1.text = currentItem.ftime
        holder.tvTime2.text = currentItem.stime
        holder.tvNumOfLess.text = (position + 1).toString()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView){
        val tvLesson: TextView = itemView.findViewById(R.id.tvLessonName)
        val tvTeacher: TextView = itemView.findViewById(R.id.tvTeacherName)
        val tvCabinet: TextView = itemView.findViewById(R.id.tvClassroom)
        val tvSubgroup: TextView = itemView.findViewById(R.id.tvSubgroup)
        val tvTime1: TextView = itemView.findViewById(R.id.time1)
        val tvTime2: TextView = itemView.findViewById(R.id.time2)
        val tvNumOfLess: TextView = itemView.findViewById(R.id.tvNumberOfLesson)

    }
}