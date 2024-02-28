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
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePerson(person: Person)

    /**
     * Get LiveData person from the storage by id
     */
    @Query("Select * FROM people WHERE (people.id == :id)")
    fun getPerson(id: Int): LiveData<Person>

    /**
     * Get const data person from the storage by id
     */
    @Query("Select * FROM people WHERE (people.id == :id)")
    fun getPersonStatic(id: Int): Person

    /**
     * Get all the people born in given month as LiveData
     */
    @Query("Select * FROM people WHERE (people.birth_month == :month)")
    fun getPeopleByMonth(month: Int): LiveData<List<Person>>

    /**
     * Get all the people born in given month as static data
     */
    @Query("Select * FROM people WHERE (people.birth_month == :month)")
    fun getPeopleByMonthStatic(month: Int): List<Person>

    /**
     * Get all the people born in a given day as LiveData
     */
    @Query("Select * FROM people WHERE (people.birth_day == :day AND people.birth_month == :month)")
    fun getPeopleByDate(day: Int, month: Int): LiveData<List<Person>>

    /**
     * Get all the people born in a given day as static data
     */
    @Query("Select * FROM people WHERE (people.birth_day == :day AND people.birth_month == :month)")
    fun getPeopleByDateStatic(day: Int, month: Int): List<Person>

    /**
     * Get all the people in the storage as LiveData
     */
    @Query("SELECT * FROM people")
    fun getAllPeople(): LiveData<List<Person>>

    /**
     * Get all the people in the storage static data
     */
    @Query("SELECT * FROM people")
    fun getAllPeopleStatic(): List<Person>
}
