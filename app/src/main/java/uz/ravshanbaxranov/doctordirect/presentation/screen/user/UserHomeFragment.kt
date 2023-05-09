package uz.ravshanbaxranov.doctordirect.presentation.screen.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentUserHomeBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.adapter.DoctorsAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.user.UserHomeViewModel

@AndroidEntryPoint
class UserHomeFragment : Fragment(R.layout.fragment_user_home) {

    private lateinit var binding: FragmentUserHomeBinding
    private val adapter by lazy { DoctorsAdapter() }
    private val viewModel: UserHomeViewModel by viewModels()
    private var username: String = ""
    private var fullName: String = ""

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentUserHomeBinding.bind(view)

        binding.doctorsRv.adapter = adapter

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
                binding.nameTv.text = "Hello ${it.firstName}"
                username = it.username
                fullName = it.firstName + " " + it.lastName
                Glide.with(binding.avatarIv)
                    .load(it.avatarUrl)
                    .placeholder(R.drawable.img_1)
                    .into(binding.avatarIv)
            }
        }

        adapter.setOnCLickListener { doctor, imageView ->
            val extra = FragmentNavigatorExtras(imageView to "shared_element")

            if (username.isBlank() && fullName.isBlank()) {
                showToast("User data could not be loaded, please try again later")
            }

            findNavController().navigate(
                UserHomeFragmentDirections.actionUserHomeScreenToDoctorFragment(
                    doctor,
                    username,
                    fullName
                ),
                extra
            )
        }




    }

}