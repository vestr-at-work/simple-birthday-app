package net.example.simplebirthdayapp.personStorage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import net.example.simplebirthdayapp.data.Person

/**
 * Data access object for storage of people.
 */
@Dao
interface personDao {
    /**
     * Add person to storage.
     */
    @Insert
    fun addPerson(person: Person)

    /**
     * Delete person from storage.
     */
    @Delete
    fun deletePerson(person: Person)

    /**
     * Update person in the storage.
     */
    @Update
    fun updatePerson(person: Person)

    /**
     * Get person from the storage by id.
     */
    @Query("Select * FROM person WHERE (person.id == :id)")
    fun getPerson(id: Int): Person

    /**
     * Get all the people born in given month.
     */
    @Query("Select * FROM person WHERE (person.birth_month == :month)")
    fun getPeopleByMonth(month: Int): List<Person>

    /**
     * Get all the people born in a given day.
     */
    @Query("Select * FROM person WHERE (person.birth_day == :day AND person.birth_month == :month)")
    fun getPeopleByDate(day: Int, month: Int): List<Person>

    /**
     * Get all the people in the storage.
     */
    @Query("SELECT * FROM person")
    fun getAllPeople(): List<Person>
}