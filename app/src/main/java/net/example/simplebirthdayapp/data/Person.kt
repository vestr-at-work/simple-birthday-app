package net.example.simplebirthdayapp.data

import java.util.Date

data class Person(
    val id: Int,
    val name: String,
    val birthday: Date,
    val yearPresent: Boolean
)
