package uz.ravshanbaxranov.doctordirect

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.LocaleList
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.app.DoctorDirectApplication
import uz.ravshanbaxranov.doctordirect.databinding.ActivityMainBinding
import uz.ravshanbaxranov.doctordirect.other.LocaleHelper
import uz.ravshanbaxranov.doctordirect.other.showLog
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.MainActivityViewModel
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        binding.navView.menu.clear()

        binding.navView.inflateMenu(R.menu.user_nav_menu)
        binding.navView.setupWithNavController(navHostFragment.navController)


        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                R.id.userHomeFragment, R.id.userAppointmentsFragment, R.id.profileFragment,
                R.id.doctorsFragment, R.id.addDoctorFragment, R.id.adminAppointmentsFragment,
                R.id.doctorHomeFragment -> {
                    binding.navView.isVisible = true
                }

                else -> {
                    binding.navView.isVisible = false
                }

            }
        }

        lifecycleScope.launch {
            viewModel.setLangFlow.collect {
                LocaleHelper(this@MainActivity).selectLang(it)
            }
        }


    }
}