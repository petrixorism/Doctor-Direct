package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentScannerResultForUserBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.adapter.AppointmentAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.user.UserScannerResultViewModel

@AndroidEntryPoint
class ScannerResultForUserFragment : Fragment(R.layout.fragment_scanner_result_for_user) {

    private val binding by viewBinding(FragmentScannerResultForUserBinding::bind)
    private val args by navArgs<ScannerResultForUserFragmentArgs>()
    private val viewModel: UserScannerResultViewModel by viewModels()
    private val adapter by lazy { AppointmentAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val user = args.user
        val gender = if (user.male) {
            getString(R.string.male)
        } else {
            getString(R.string.female)
        }

        viewModel.getAppoints(user.username)

        Glide.with(binding.avatarIv)
            .load(user.avatarUrl)
            .placeholder(R.drawable.img_1)
            .into(binding.avatarIv)

        binding.patientNameTv.text = user.getFullName()
        binding.emailTv.text = user.email
        binding.genderTv.text = gender
        binding.birthTv.text = user.birthDate



        viewModel.errorFlow.onEach {
            val msg = it.toIntOrNull()
            if (msg == null) {
                showToast(it)
            } else {
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
                adapter.submitList(it)
                binding.emptyTv.isVisible = it.isEmpty()
            }
        }

        binding.appointmentRv.adapter = adapter

        adapter.setOnCLickListener {
            findNavController().navigate(
                ScannerResultForUserFragmentDirections.actionScannerResultForUserFragmentToAppointmentQrFragment(
                    it
                )
            )
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.patientNameTv.text = user.getFullName()
        binding.birthTv.text = user.birthDate
        binding.emailTv.text = user.email


        binding.genderTv.text = gender


    }


}