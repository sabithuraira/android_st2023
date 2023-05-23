package bps.sumsel.st2023

//import bps.sumsel.st2023.room.MIGRATION_1_2
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import bps.sumsel.st2023.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

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

    private fun setupNav() {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_setting,
                R.id.navigation_notifications, R.id.navigation_sls
                -> binding.navView.visibility = View.VISIBLE
                else -> binding.navView.visibility = View.GONE
            }

            when (destination.id) {
                R.id.navigation_home, R.id.navigation_setting,
                R.id.navigation_notifications, R.id.navigation_sls,
                R.id.navigation_splash, R.id.navigation_login
                -> supportActionBar?.hide()
                R.id.detailSlsFragment, R.id.rumahTanggaFragment -> {
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                else -> {
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
            }
        }
    }

    fun setLoading(data: Boolean) {
        binding.progressBar.visibility = if (data) View.VISIBLE else View.GONE
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }
}