package com.example.todo

import MainAdapter
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
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
    lateinit var pref:SharedPreferences
    lateinit var profile:ImageView
    var nightMode=false
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
        profile=findViewById(R.id.profile)
        pref=getSharedPreferences("MODE", Context.MODE_PRIVATE)
        nightMode=pref.getBoolean("night",false)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager=LinearLayoutManager(this)
        if(nightMode){
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
        GlobalScope.launch(Dispatchers.IO) {
            count=getcount()
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
        profile.setOnClickListener {
            if(!nightMode){
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                nightMode=true
            }else{
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                nightMode=false
            }
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
        } catch (e: Exception) {
            // Handle any errors that occurred while retrieving the data
        }
        val filteredList = userList.filter { user ->
            user.id != auth.uid
        }
        return filteredList
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

    override fun onStop() {
        super.onStop()
        val editor=pref.edit()
        editor.putBoolean("night",nightMode)
    }

}