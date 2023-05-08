package uz.ravshanbaxranov.doctordirect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.ravshanbaxranov.doctordirect.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        binding.navView.menu.clear()

        binding.navView.inflateMenu(R.menu.admin_nav_menu)
        binding.navView.setupWithNavController(navHostFragment.navController)


        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            // invisible some fragments later
            when (destination.id) {

                R.id.userHomeFragment, R.id.userAppointmentsFragment, R.id.userProfileFragment,
                R.id.doctorsFragment, R.id.addDoctorFragment -> {
                    binding.navView.isVisible = true
                }

                else -> {
                    binding.navView.isVisible = false
                }

            }
        }

    }
}