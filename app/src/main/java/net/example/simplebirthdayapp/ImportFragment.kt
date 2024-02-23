package net.example.simplebirthdayapp

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.data.Person
import net.example.simplebirthdayapp.databinding.ActivityMainBinding
import net.example.simplebirthdayapp.databinding.FragmentImportBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase

class ImportFragment : Fragment() {

    private lateinit var binding: FragmentImportBinding
    private lateinit var database: PersonDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        database = PersonDatabase.getDatabase(requireContext())

        return inflater.inflate(R.layout.fragment_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        class ContactReader(private val contentResolver: ContentResolver) {

            @SuppressLint("Range")
            fun readContacts(): List<Person> {
                val contacts = mutableListOf<Person>()

                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null
                )

                cursor?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val id =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                        val name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                        var birthday: String = ""
                        val birthdayCursor = contentResolver.query(
                            ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            arrayOf(id, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE),
                            null
                        )

                        birthdayCursor?.use { birthdayCursor ->
                            while (birthdayCursor.moveToNext()) {
                                val event = birthdayCursor.getString(
                                    birthdayCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE)
                                )
                                val type = birthdayCursor.getInt(
                                    birthdayCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE)
                                )

                                if (type == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {
                                    birthday = event
                                    break
                                }
                            }
                        }

                        contacts.add(Person(0, name, birthday.toInt(), birthday.toInt(), null))
                    }
                }

                return contacts
            }
        }

        var contactsList : List<Person>? = null //context?.let { ContactReader(it.contentResolver).readContacts() }
        //TODO
        if (contactsList != null) {
            lifecycleScope.launch {
                for (person in contactsList) {
                    database.personDao().addPerson(person)
                }

                val text = "@string/people_added"
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()

                findNavController().popBackStack()
            }
        }
    }
}
