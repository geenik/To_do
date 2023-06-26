package com.example.todo.model

data class User(val id:String,
                val name:String,
                val email:String,
                val age:Int){
    constructor() : this("", "", "", 0)
}
