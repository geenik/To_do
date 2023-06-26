package com.example.todo.model

data class Task(
    val userid: String,
    val title: String,
    val description: String,
    var pending: Boolean
) {
    constructor() : this("", "", "", true)
}
