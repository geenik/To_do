package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.todo.databinding.ActivityAddTaskBinding
import com.example.todo.model.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddTask : AppCompatActivity() {
    val auth=Firebase.auth
    val database = FirebaseDatabase.getInstance().reference
    lateinit var binding:ActivityAddTaskBinding
    override fun onStart() {
        super.onStart()
        binding.taskscount.text= "You have total ${MainActivity.count} tasks"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.savebtn.setOnClickListener {
            val title=binding.taskheading.text.toString()
            val description=binding.taskdescription.text.toString()
            if(title.isEmpty()||description.isEmpty()){
                startActivity(Intent(this@AddTask,tasks::class.java))
                return@setOnClickListener
            }
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main){
                    binding.savebtn.visibility=View.GONE
                    binding.progressbar.visibility=View.VISIBLE
                }
                NetworkIdlingResource.increment()
                addtask(title,description)
                NetworkIdlingResource.decrement()
                withContext(Dispatchers.Main){
                    binding.progressbar.visibility=View.GONE
                    binding.taskheading.text?.clear()
                    binding.taskdescription.text?.clear()
                    startActivity(Intent(this@AddTask,tasks::class.java))
                    MainActivity.count++
                    binding.savebtn.visibility=View.VISIBLE
                }
            }
        }
        binding.backbtn.setOnClickListener {
            finish()
        }
    }

     suspend fun addtask(title: String, description: String) {
        val task=Task(auth.uid!!,title,description,true)
       database.child("tasks").push().setValue(task).await()
    }
}