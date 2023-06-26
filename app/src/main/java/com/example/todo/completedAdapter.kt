package com.example.todo


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.model.Task


class CompletedTaskAdapter(private var taskList: List<Task>) :
    RecyclerView.Adapter<CompletedTaskAdapter.TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.taskcomplete_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val (_, title, description) = taskList[position]
        holder.headingTextView.text = title
        holder.descriptionTextView.text = description
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var headingTextView: TextView
        var descriptionTextView: TextView
        init {
            headingTextView = itemView.findViewById(R.id.heading)
            descriptionTextView = itemView.findViewById(R.id.description)
        }
    }

}
