package uz.ravshanbaxranov.doctordirect.presentation.screen.admin

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
import uz.ravshanbaxranov.doctordirect.databinding.FragmentAdminAppointmentsBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.adapter.AppointmentAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.admin.AdminAppointmentViewModel

@AndroidEntryPoint
class AdminAppointmentsFragment : Fragment(R.layout.fragment_admin_appointments) {

    private val viewModel: AdminAppointmentViewModel by viewModels()
    private lateinit var binding: FragmentAdminAppointmentsBinding
    private val adapter by lazy { AppointmentAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentAdminAppointmentsBinding.bind(view)


        binding.appointmentsRv.adapter = adapter

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
            viewModel.appointmentsListStateFlow.collect {
                adapter.submitList(it)
                binding.emptyTv.isVisible = it.isEmpty()
            }
        }

        adapter.setOnCLickListener {
            findNavController().navigate(
                AdminAppointmentsFragmentDirections.actionAdminAppointmentsFragmentToAppointmentQrFragment(
                    it
                )
            )
        }

    }

}