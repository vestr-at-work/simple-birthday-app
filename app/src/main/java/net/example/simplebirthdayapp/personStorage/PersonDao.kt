package net.example.simplebirthdayapp.personStorage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.example.simplebirthdayapp.data.Person

/**
 * Data access object for storage of people
 */
@Dao
interface PersonDao {
    /**
     * Add person to storage
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPerson(person: Person)

    /**
     * Delete person from storage
     */
    @Delete
    suspend fun deletePerson(person: Person)

    /**
     * Update person in the storage
     */
    @Update
    suspend fun updatePerson(person: Person)

    /**
     * Get person from the storage by id
     */
    @Query("Select * FROM people WHERE (people.id == :id)")
    fun getPerson(id: Int): LiveData<Person>

    /**
     * Get all the people born in given month
     */
    @Query("Select * FROM people WHERE (people.birth_month == :month)")
    fun getPeopleByMonth(month: Int): LiveData<List<Person>>

    /**
     * Get all the people born in a given day
     */
    @Query("Select * FROM people WHERE (people.birth_day == :day AND people.birth_month == :month)")
    fun getPeopleByDate(day: Int, month: Int): LiveData<List<Person>>

    /**
     * Get all the people in the storage
     */
    @Query("SELECT * FROM people")
    fun getAllPeople(): LiveData<List<Person>>
}
