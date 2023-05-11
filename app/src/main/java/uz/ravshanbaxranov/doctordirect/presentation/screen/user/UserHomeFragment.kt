package uz.ravshanbaxranov.doctordirect.presentation.screen.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
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
    private var user: User? = null

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentUserHomeBinding.bind(view)

        binding.doctorsRv.adapter = adapter

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
            viewModel.doctorsListStateFlow.collect {
                adapter.submitList(it)
            }
        }
        lifecycleScope.launch {
            viewModel.userDataStateFlow.collect {
                user = it
                binding.nameTv.append(it.firstName)
                username = it.username
                fullName = it.firstName + " " + it.lastName
                Glide.with(binding.avatarIv)
                    .load(it.avatarUrl)
                    .placeholder(R.drawable.img_1)
                    .into(binding.avatarIv)
            }
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDoctors(s.toString().lowercase())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        adapter.setOnCLickListener { doctor, imageView ->

            if (doctor.available) {
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
            } else {
                showToast(getString(R.string.doctor_not_available))
            }


        }

        binding.scannerFba.setOnClickListener {
            if (user != null && ((user?.uid ?: "") != "")) {
                findNavController().navigate(
                    UserHomeFragmentDirections.actionUserHomeFragmentToUserDetailsFragment(
                        user!!
                    )
                )
            } else {
                showToast(getString(R.string.user_details_not_found))
            }
        }


    }

}