package com.onlylose.schedule

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private val items: List<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.menuItemText)

        fun bind(item: String) {
            textView.text = item
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
        init {
            itemView.setOnClickListener {
                var scaleDown = ObjectAnimator.ofFloat(itemView, "scaleX", 0.9f)
                var scaleDownY = ObjectAnimator.ofFloat(itemView, "scaleY", 0.9f)

                scaleDown
                scaleDownY.duration = 100

                val scaleUp = ObjectAnimator.ofFloat(itemView, "scaleX", 1.0f)
                val scaleUpY = ObjectAnimator.ofFloat(itemView, "scaleY", 1.0f)

                scaleUp.duration = 100
                scaleUpY.duration = 100

                itemView.setOnClickListener {
                    // Запускаем анимацию с уменьшением
                    AnimatorSet().apply {
                        play(scaleDown).with(scaleDownY)
                        start()
                    }

                    // Ожидаем завершение анимации и запускаем анимацию увеличения
                    Handler(Looper.getMainLooper()).postDelayed({
                        AnimatorSet().apply {
                            play(scaleUp).with(scaleUpY)
                            start()
                        }
                    }, 150)
                }
            }
        }
    }
}
