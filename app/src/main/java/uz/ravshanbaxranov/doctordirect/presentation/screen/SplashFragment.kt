package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentSplashBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.mytaxi.presentation.viewmodel.SplashViewModel

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var bottomNav: BottomNavigationView
    private val binding by viewBinding(FragmentSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bottomNav = requireActivity().findViewById(R.id.nav_view)


        lifecycleScope.launch {
            viewModel.adminScreenFlow.collect {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(R.menu.admin_nav_menu)

                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToAdminNavigation())
            }
        }
        lifecycleScope.launch {
            viewModel.doctorScreenFlow.collect {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(R.menu.doctor_nav_menu)

                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToDoctorNavigation())
            }
        }
        lifecycleScope.launch {
            viewModel.userScreenFlow.collect {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(R.menu.user_nav_menu)
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToUserNavigation())
            }
        }
        lifecycleScope.launch {
            viewModel.onboardingFlow.collect {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnboardParentFragment())
            }
        }
        lifecycleScope.launch {
            viewModel.errorFlow.collect {
                showToast(it)
            }
        }

        lifecycleScope.launch {
            viewModel.loadingStateFlow.collect {
                binding.loadingPb.isVisible = it
            }
        }

        lifecycleScope.launch {
            viewModel.loginScreenFlow.collect {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }

    }

}