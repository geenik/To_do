package com.example.todo

import android.app.Fragment
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.model.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class completed : Fragment() {
    val database = FirebaseDatabase.getInstance().reference
    val auth= Firebase.auth
    var tasks=mutableListOf<Task>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview=inflater.inflate(R.layout.fragment_completed, container, false)
        val recyclerView=rootview.findViewById<RecyclerView>(R.id.completedrecyclerview)
        GlobalScope.launch {
            tasks= userlist() as MutableList<Task>
            withContext(Dispatchers.Main){
                val adapter = CompletedTaskAdapter(tasks)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(activity)
            }
        }
        return rootview
    }
    suspend fun userlist(): List<Task> {
        val userList = mutableListOf<Task>()
        try {
            val dataSnapshot = database.child("tasks").get().await()

            for (userSnapshot in dataSnapshot.children) {
                val task = userSnapshot.getValue(Task::class.java)
                if (task != null && !task.pending) {
                    if(task.userid==auth.uid)
                        task.let { userList.add(it) }
                }
            }
        } catch (e: Exception) {
            // Handle any errors that occurred while retrieving the data
        }
        return userList
    }
}