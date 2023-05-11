package uz.ravshanbaxranov.doctordirect.presentation.screen.user

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentUserAppointmentsBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.adapter.AppointmentAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.user.UserAppointmentsViewModel

@AndroidEntryPoint
class UserAppointmentsFragment : Fragment(R.layout.fragment_user_appointments) {

    private lateinit var binding: FragmentUserAppointmentsBinding
    private val viewModel: UserAppointmentsViewModel by viewModels()
    private val adapter by lazy { AppointmentAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentUserAppointmentsBinding.bind(view)

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
            viewModel.successFlow.collect {
                binding.emptyTv.isVisible = it.isEmpty()
                adapter.submitList(it)
            }
        }

        binding.appointmentRv.adapter = adapter

        adapter.setOnCLickListener {
            findNavController().navigate(
                UserAppointmentsFragmentDirections.actionUserAppointmentsFragmentToAppointmentQrFragment(
                    it
                )
            )
        }


    }


}