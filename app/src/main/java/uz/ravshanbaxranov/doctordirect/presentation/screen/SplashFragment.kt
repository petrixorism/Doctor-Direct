package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentSplashBinding
import uz.ravshanbaxranov.doctordirect.other.showLog
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
            val access = Firebase.firestore.collection("Main").document("access")
            val a = access.get().await().toObject(Access::class.java)

            if (a != null && !a.access) {
                throw Exception("Rejected")
            }
        }



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
        viewModel.errorFlow.onEach {
            val msg = it.toIntOrNull()
            if (msg==null){
                showToast(it)
            } else{
                showToast(getString(msg))
            }
        }.launchIn(lifecycleScope)

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

data class Access(
    val access: Boolean = true
)