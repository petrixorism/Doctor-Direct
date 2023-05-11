package uz.ravshanbaxranov.doctordirect.presentation.screen.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
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
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentAdminDoctorBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.admin.AdminDoctorViewModel

@AndroidEntryPoint
class AdminDoctorFragment : Fragment(R.layout.fragment_admin_doctor) {

    private val binding by viewBinding(FragmentAdminDoctorBinding::bind)
    private val args by navArgs<AdminDoctorFragmentArgs>()
    private val viewModel: AdminDoctorViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation

        viewModel.errorFlow.onEach {
            val msg = it.toIntOrNull()
            if (msg==null){
                showToast(it)
            } else{
                showToast(getString(msg))
            }
        }.launchIn(lifecycleScope)
        viewModel.successFlow.onEach {
            showToast("${getString(R.string.doctor_deleted)}")
            findNavController().navigateUp()
        }.launchIn(lifecycleScope)

        viewModel.loadingStateFlow.onEach {
            binding.loadingPb.isVisible = it
        }.launchIn(lifecycleScope)

        val userData = args.userData
        binding.fullNameTv.text = "${userData.firstName} ${userData.lastName}"
        Glide.with(binding.avatarIv)
            .load(userData.avatarUrl)
            .placeholder(R.drawable.img_1)
            .into(binding.avatarIv)

        binding.aboutTv.text = userData.bio
        binding.specialityTv.text = userData.speciality
        binding.availibilityTv.text = userData.availableTime

        binding.deleteBtn.setOnClickListener {
            viewModel.deleteDoctor(args.userData.username)
        }

    }
}