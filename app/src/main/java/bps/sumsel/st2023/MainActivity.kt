package bps.sumsel.st2023

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import bps.sumsel.st2023.databinding.ActivityMainBinding
import bps.sumsel.st2023.room.St2023Database

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbRoom: St2023Database
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRoom = Room.databaseBuilder(this, St2023Database::class.java, "room_db")
            .fallbackToDestructiveMigration().build()

        setContentView(binding.root)
        this.setupNav()
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_splash,
//                R.id.navigation_login,
//                R.id.navigation_home,
//                R.id.navigation_sls,
//                R.id.navigation_dashboard
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    private fun setupNav(){
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_dashboard,
                R.id.navigation_notifications, R.id.navigation_sls
                -> binding.navView.visibility = View.VISIBLE
                else -> binding.navView.visibility = View.GONE
            }
        }
    }
}