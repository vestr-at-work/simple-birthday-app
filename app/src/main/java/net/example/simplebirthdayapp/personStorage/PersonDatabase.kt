package net.example.simplebirthdayapp.personStorage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.example.simplebirthdayapp.data.Person

/**
 * Database declared for the Room API
 */
@Database(entities = [Person::class], version = 1)
abstract class PersonDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    /**
     * Database singleton
     */
    companion object {
        @Volatile
        private var INSTANCE: PersonDatabase? = null
        fun getDatabase(context: Context): PersonDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context.applicationContext, PersonDatabase::class.java, "person_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}