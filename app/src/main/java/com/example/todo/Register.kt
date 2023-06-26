package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.todo.databinding.ActivityRegisterBinding
import com.example.todo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    private lateinit var auth:FirebaseAuth
    val database = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signinBtn.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val age = binding.ageInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (name.isNotEmpty() && age.toIntOrNull()!=null && email.isNotEmpty() && password.isNotEmpty()) {
               register(email,password,name,age.toInt())
            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.forsigin.setOnClickListener {
            startActivity(Intent(this,SignIn::class.java))
            finish()
        }
        auth=Firebase.auth
    }

    private fun register(email:String,password:String,name:String,age:Int){
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Register Successfully! Sign in to Continue",
                        Toast.LENGTH_SHORT,
                    ).show()
                    // Sign in success, update UI with the signed-in user's information
                    auth=Firebase.auth
                    startActivity(Intent(this,MainActivity::class.java))
                    adduser(name,email,age)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun adduser(name: String, email: String, age: Int) {
        val uid=auth.currentUser!!.uid
        val user= User(uid,name,email,age)
        database.child("users").child(uid).setValue(user)
    }

}