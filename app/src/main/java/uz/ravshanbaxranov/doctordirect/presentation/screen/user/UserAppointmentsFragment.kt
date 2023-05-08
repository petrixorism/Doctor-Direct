package uz.ravshanbaxranov.doctordirect.presentation.screen.user

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentUserAppointmentsBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.adapter.AppointmentAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.UserAppointmentsViewModel

@AndroidEntryPoint
class UserAppointmentsFragment : Fragment(R.layout.fragment_user_appointments) {

    private val binding by viewBinding(FragmentUserAppointmentsBinding::bind)
    private val viewModel: UserAppointmentsViewModel by viewModels()
    private val adapter by lazy { AppointmentAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launch {
            viewModel.errorFlow.collect{
                showToast(it)
            }
        }

        lifecycleScope.launch {
            viewModel.loadingStateFlow.collect{
                binding.loadingPb.isVisible = it
            }
        }

        lifecycleScope.launch {
            viewModel.successFlow.collect{
                adapter.submitList(it)
            }
        }

        binding.appointmentRv.adapter = adapter

    }


}