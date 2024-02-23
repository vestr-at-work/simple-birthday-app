package net.example.simplebirthdayapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Table entity for the Room API
 */
@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "birth_day") val birthDay: Int,
    @ColumnInfo(name = "birth_month") val birthMonth: Int,
    @ColumnInfo(name = "birth_year") val birthYear: Int?
)
