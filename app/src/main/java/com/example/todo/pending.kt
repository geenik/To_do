package com.example.todo

import android.app.Application
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
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

class pending() : Fragment(),PendingTaskAdapter.OnTaskClickListener{
    val database = FirebaseDatabase.getInstance().reference
    val auth= Firebase.auth
    var tasks=mutableListOf<Task>()
    lateinit var taskscount:TextView
    lateinit var recyclerView: RecyclerView
    lateinit var progressbar:ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview=inflater.inflate(R.layout.fragment_pending, container, false)
        recyclerView=rootview.findViewById(R.id.pendingrecyclerview)
        taskscount=activity.findViewById(R.id.taskscount)
        progressbar=rootview.findViewById(R.id.progressbar)
        GlobalScope.launch {
            tasks= userlist() as MutableList<Task>
            withContext(Dispatchers.Main){
                updaterecyclerview()
            }
        }
        return rootview
    }

    private  fun pending.updaterecyclerview() {
        val adapter = PendingTaskAdapter(tasks, this@pending)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        taskscount.text="You have ${MainActivity.count} tasks pending"
    }

    suspend fun userlist(): List<Task> {
        val userList = mutableListOf<Task>()
        var count=0
        try {
            val dataSnapshot = database.child("tasks").get().await()
            for (userSnapshot in dataSnapshot.children) {
                val task = userSnapshot.getValue(Task::class.java)
                if (task != null && task.pending) {
                    if(task.userid==auth.uid) {
                        count++
                        task.let { userList.add(it) }
                    }
                }
            }
            MainActivity.count=count
        } catch (e: Exception) {
            // Handle any errors that occurred while retrieving the data
        }
        return userList
    }

    override fun onDeleteClick(position: Int) {
        progressbar.visibility=View.VISIBLE
        try {
           GlobalScope.launch {
                val dataSnapshot = database.child("tasks").get().await()
                var i = 0
                for (userSnapshot in dataSnapshot.children) {
                    val task = userSnapshot.getValue(Task::class.java)
                    if (i == position && task!!.pending &&task.userid==auth.uid) {
                        userSnapshot.ref.removeValue().await()
                        break
                    }

                    if (task != null && task.pending) {
                        if(task.userid==auth.uid)
                            i++
                    }
                }
               tasks= userlist() as MutableList<Task>
               withContext(Dispatchers.Main){
                   updaterecyclerview()
                   progressbar.visibility=View.GONE
               }
            }
        } catch (e: Exception) {
            // Handle any errors that occurred while deleting the task
        }
    }

    override fun onTickClick(position: Int) {
        progressbar.visibility=View.VISIBLE
        try {
            GlobalScope.launch {
                val dataSnapshot = database.child("tasks").get().await()

                var i = 0
                for (userSnapshot in dataSnapshot.children) {
                    val task = userSnapshot.getValue(Task::class.java)
                    if (i == position && task!!.pending && task.userid==auth.uid) {
                        task!!.pending=false
                        userSnapshot.ref.setValue(task).await()
                        break
                    }

                    if (task != null && task.pending) {
                        if(task.userid==auth.uid)
                            i++
                    }
                }
                tasks= userlist() as MutableList<Task>
                withContext(Dispatchers.Main){
                    updaterecyclerview()
                    progressbar.visibility=View.GONE
                }
            }
        } catch (e: Exception) {
            // Handle any errors that occurred while deleting the task
        }
    }


}