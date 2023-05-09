package uz.ravshanbaxranov.doctordirect.presentation.screen.doctor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentDoctorHomeBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.adapter.AppointmentAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.doctor.DoctorHomeViewModel

@AndroidEntryPoint
class DoctorHomeFragment : Fragment(R.layout.fragment_doctor_home) {

    private val adapter by lazy { AppointmentAdapter("doctor") }
    private val viewModel: DoctorHomeViewModel by viewModels()
    private lateinit var binding: FragmentDoctorHomeBinding

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentDoctorHomeBinding.bind(view)

        binding.appointmentRv.adapter = adapter

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
            viewModel.doctorsListStateFlow.collect {
                adapter.submitList(it)
            }
        }
        lifecycleScope.launch {
            viewModel.userDataStateFlow.collect {
                binding.nameTv.text = "Dr.${it.firstName}"
//                username = it.username
//                fullName = it.firstName + " " + it.lastName
                Glide.with(binding.avatarIv)
                    .load(it.avatarUrl)
                    .placeholder(R.drawable.img_1)
                    .into(binding.avatarIv)
            }
        }

        adapter.setOnCLickListener {
            findNavController().navigate(
                DoctorHomeFragmentDirections.actionDoctorHomeScreenToReviewAppointmentFragment(
                    it
                )
            )
        }

        binding.scannerFba.setOnClickListener {
            val navigation = requireActivity().findViewById<FragmentContainerView>(R.id.nav_host_fragment_activity_main)

            parentFragment?.findNavController()?.navigate(R.id.action_scannerFragment_self)

//            findNavController().navigate(DoctorHomeFragmentDirections.actionDoctorHomeFragmentToScannerFragment())
        }

    }


}