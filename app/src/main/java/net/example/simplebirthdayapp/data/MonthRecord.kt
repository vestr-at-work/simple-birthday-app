package net.example.simplebirthdayapp.data

import java.time.Month

data class MonthRecord(
    val month: Month,
    val birthdays: List<Person>
)
