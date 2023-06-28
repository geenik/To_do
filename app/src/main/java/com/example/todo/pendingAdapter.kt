package com.example.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.model.Task


class PendingTaskAdapter(private var taskList: List<Task>,private val listener: OnTaskClickListener) :
    RecyclerView.Adapter<PendingTaskAdapter.TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val (_, title, description) = taskList[position]
        holder.headingTextView.text = title
        holder.descriptionTextView.text = description
        holder.deleteImageView.setOnClickListener {
            listener.onDeleteClick(position)
        }
        holder.tickImageView.setOnClickListener {
            listener.onTickClick(position)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var headingTextView: TextView
        var descriptionTextView: TextView
        var deleteImageView: ImageView
        var tickImageView: ImageView

        init {
            headingTextView = itemView.findViewById(R.id.heading)
            descriptionTextView = itemView.findViewById(R.id.description)
            deleteImageView = itemView.findViewById(R.id.deletebtn)
            tickImageView = itemView.findViewById(R.id.tick)
        }

    }
    interface OnTaskClickListener {
        fun onDeleteClick(position: Int)
        fun onTickClick(position: Int)
    }

}
