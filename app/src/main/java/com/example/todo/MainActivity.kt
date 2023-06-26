package com.example.todo

import MainAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.model.Task
import com.example.todo.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    lateinit var adapter:MainAdapter
    val database = FirebaseDatabase.getInstance().reference
     val auth=Firebase.auth
    lateinit var fab:FloatingActionButton
    lateinit var taskcount:TextView
 companion object{
     var count=0
 }
    override fun onStart() {
        super.onStart()
        taskcount.text="You have $count tasks pending"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab=findViewById(R.id.fab)
        taskcount=findViewById(R.id.tasks)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager=LinearLayoutManager(this)
        GlobalScope.launch(Dispatchers.IO) {
            var count=getcount()
            val userList = userlist()
            runOnUiThread{
                adapter = MainAdapter(userList)
                recyclerView.adapter = adapter
                taskcount.text="You have ${Companion.count} tasks pending"
            }
        }
        fab.setOnClickListener{
            startActivity(Intent(this,AddTask::class.java))
        }

    }
    suspend fun userlist(): List<User> {
        val userList = mutableListOf<User>()
        try {
            val dataSnapshot = database.child("users").get().await()

            for (userSnapshot in dataSnapshot.children) {
                val user = userSnapshot.getValue(User::class.java)
                user?.let { userList.add(it) }
            }
            Log.d(TAG, userList.size.toString())
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            // Handle any errors that occurred while retrieving the data
        }
        for(user in userList){
            if(user.id==auth.uid){
                userList.remove(user)
            }
        }
        return userList
    }
    suspend fun getcount(): Int {
        try {
            val dataSnapshot = database.child("tasks").get().await()

            for (userSnapshot in dataSnapshot.children) {
                val task = userSnapshot.getValue(Task::class.java)
                if (task != null) {
                    if(task.userid==auth.uid && task.pending) {
                        count++
                    }
                }
            }
        } catch (e: Exception) {
            // Handle any errors that occurred while retrieving the data
        }
        return count
    }

}