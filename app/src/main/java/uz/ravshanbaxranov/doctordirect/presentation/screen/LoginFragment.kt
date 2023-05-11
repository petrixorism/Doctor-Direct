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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentLoginBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var bottomNav: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNav = requireActivity().findViewById(R.id.nav_view)


        lifecycleScope.launch {
            viewModel.adminScreenFlow.collect {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(R.menu.admin_nav_menu)

                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAdminNavigation())
            }
        }
        lifecycleScope.launch {
            viewModel.doctorScreenFlow.collect {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(R.menu.doctor_nav_menu)
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToDoctorNavigation())
            }
        }
        lifecycleScope.launch {
            viewModel.userScreenFlow.collect {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(R.menu.user_nav_menu)
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUserNavigation())
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
                binding.signinBtn.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            viewModel.successFlow.collect {

            }
        }

        binding.signupTv.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.signinBtn.setOnClickListener {
            viewModel.login(binding.nameEt.text.toString(), binding.passwordTet.text.toString())
        }

    }


}