package net.example.simplebirthdayapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.example.simplebirthdayapp.databinding.ActivityMainBinding
import net.example.simplebirthdayapp.personStorage.PersonDatabase
import net.example.simplebirthdayapp.data.Person

const val CHANNEL_ID = "birthday_alert"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: PersonDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        // Notifications
        createNotificationChannel()

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.add_new_person), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        binding.fab.setOnClickListener { view ->
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.NewPersonFragment)
        }

        database = PersonDatabase.getDatabase(this)
        GlobalScope.launch {
/*
            database.personDao().addPerson(Person(0, "Marek", 2, 2, 2022))
            database.personDao().addPerson(Person(0, "Today", 27, 2, 2024))
            database.personDao().addPerson(Person(0, "Tomorrow", 28, 2, 2024))
            database.personDao().addPerson(Person(0, "Pepa", 8, 3, 2002))
            database.personDao().addPerson(Person(0, "Zítřek Again", 25, 2, 0))
            database.personDao().addPerson(Person(0, "David Pavid", 13, 7, 2024))
            database.personDao().addPerson(Person(0, "David Pavit", 14, 9, 204))
            database.personDao().addPerson(Person(0, "Pavid Davit", 20, 2, 2000))
            database.personDao().addPerson(Person(0, "Pavid Davit", 20, 8, 100))
            database.personDao().addPerson(Person(0, "Worker", 8, 5, 2024))

 */
        }
        /*val calendar = Calendar.getInstance()
        val startMillis: Long = calendar.timeInMillis

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, startMillis + (60 * 60 * 1000))
            put(CalendarContract.Events.TITLE, "Meeting")
            put(CalendarContract.Events.DESCRIPTION, "Discuss project details")
            put(CalendarContract.Events.CALENDAR_ID, 0)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        */
        // can be used for system calendar???
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_import -> {
                navigateToFragment(R.id.ImportFragment)
                true
            }
            R.id.action_settings -> {
                navigateToFragment(R.id.SettingsFragment)
                true
            }
            R.id.action_about -> {
                navigateToFragment(R.id.AboutFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun navigateToFragment(destinationId: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        val currentFragment = navController.currentDestination?.id

        if (currentFragment != null) {
            navController.popBackStack(currentFragment, true)
        }
        navController.navigate(destinationId)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
