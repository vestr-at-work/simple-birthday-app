package net.example.simplebirthdayapp.personStorage

import net.example.simplebirthdayapp.data.Person
import java.util.Date

/**
 * API for storage of persons.
 */
interface personStorage {
    /**
     * Add person to storage.
     */
    fun addPerson(person: Person)

    /**
     * Delete person from storage.
     */
    fun deletePerson(person: Person)

    /**
     * Update person in the storage.
     */
    fun updatePerson(person: Person)

    /**
     * Get person from the storage by id.
     */
    fun getPerson(id: Int): Result<Person>

    /**
     * Get all the people born in given month.
     */
    fun getPeopleByMonth(month: Int): Result<List<Person>>

    /**
     * Get all the people born in a given day.
     */
    fun getPeopleByDate(date: Date): Result<List<Person>>

    /**
     * Get all the people in the storage.
     */
    fun getAllPeople(): Result<List<Person>>
}