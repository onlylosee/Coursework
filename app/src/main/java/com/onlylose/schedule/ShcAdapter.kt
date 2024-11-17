package com.onlylose.schedule

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView

data class DataClass(var ftime: String?, var firstLessonName : String, var firstLessonTeacher : String?, var stime: String?, var firstLessonCabinet : String?)



class MyAdapter(private val newList: ArrayList<DataClass>) : RecyclerView.Adapter<MyAdapter.MyViewHolder> () {
    private lateinit var a : ScheduleFragment
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = newList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newList[position]
        holder.tvLesson1.text = currentItem.firstLessonName
        holder.tvTeacher1.text = currentItem.firstLessonTeacher
        holder.tvCabinet1.text = currentItem.firstLessonCabinet
        holder.tvTime1.text = currentItem.ftime
        holder.tvTime2.text = currentItem.stime
        holder.tvNumOfLess.text = position.toString()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView){
        val tvLesson1: TextView = itemView.findViewById(R.id.tvLessonName)
        val tvTeacher1: TextView = itemView.findViewById(R.id.tvTeacherName)
        val tvCabinet1: TextView = itemView.findViewById(R.id.tvClassroom)
        val tvTime1: TextView = itemView.findViewById(R.id.time1)
        val tvTime2: TextView = itemView.findViewById(R.id.time2)
        val tvNumOfLess: TextView = itemView.findViewById(R.id.tvNumberOfLesson)

    }
}