package com.example.todo

import android.app.Application
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview=inflater.inflate(R.layout.fragment_pending, container, false)
        recyclerView=rootview.findViewById(R.id.pendingrecyclerview)
        taskscount=activity.findViewById(R.id.taskscount)
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
    }

    suspend fun userlist(): List<Task> {
        val userList = mutableListOf<Task>()
        try {
            val dataSnapshot = database.child("tasks").get().await()

            for (userSnapshot in dataSnapshot.children) {
                val task = userSnapshot.getValue(Task::class.java)
                if (task != null && task.pending) {
                    if(task.userid==auth.uid)
                        task.let { userList.add(it) }
                }
            }
        } catch (e: Exception) {
            // Handle any errors that occurred while retrieving the data
        }
        return userList
    }

    override fun onDeleteClick(position: Int) {
        try {

           GlobalScope.launch {
                val dataSnapshot = database.child("tasks").get().await()

                var i = 0
                for (userSnapshot in dataSnapshot.children) {
                    if (i == position) {
                        userSnapshot.ref.removeValue().await()
                        break
                    }
                    i++
                }
               tasks= userlist() as MutableList<Task>
               withContext(Dispatchers.Main){
                   updaterecyclerview()
               }
               MainActivity.count--
            }
        } catch (e: Exception) {
            MainActivity.count++
            // Handle any errors that occurred while deleting the task
        }
        taskscount.text="You have ${MainActivity.count} tasks pending"
    }

    override fun onTickClick(position: Int) {
        try {
            GlobalScope.launch {
                val dataSnapshot = database.child("tasks").get().await()

                var i = 0
                for (userSnapshot in dataSnapshot.children) {
                    if (i == position) {
                        val task = userSnapshot.getValue(Task::class.java)
                        task!!.pending=false
                        userSnapshot.ref.setValue(task)
                        break
                    }
                    i++
                }
                tasks= userlist() as MutableList<Task>
                withContext(Dispatchers.Main){
                    updaterecyclerview()
                }
            }
            MainActivity.count--
        } catch (e: Exception) {
            MainActivity.count++
            // Handle any errors that occurred while deleting the task
        }
        taskscount.text="You have ${MainActivity.count} tasks pending"
    }


}