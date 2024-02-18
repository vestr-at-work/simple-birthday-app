package net.example.simplebirthdayapp.personStorage

import androidx.room.Database
import androidx.room.RoomDatabase
import net.example.simplebirthdayapp.data.Person

@Database(entities = [Person::class], version = 1)
abstract class PersonDatabase : RoomDatabase() {
        abstract fun personDao(): PersonDao
}
