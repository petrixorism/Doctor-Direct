package uz.ravshanbaxranov.doctordirect.presentation.screen.admin

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentDoctorsBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.adapter.DoctorsAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.DoctorsViewModel

@AndroidEntryPoint
class DoctorsFragment : Fragment(R.layout.fragment_doctors) {

    private val binding by viewBinding(FragmentDoctorsBinding::bind)
    private val viewModel: DoctorsViewModel by viewModels()
    private val adapter by lazy { DoctorsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.addDocFba.setOnClickListener {
            findNavController().navigate(DoctorsFragmentDirections.actionDoctorsFragmentToScannerFragment())
        }

        binding.doctorsRv.adapter = adapter
        binding.doctorsRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            viewModel.errorFlow.collect {
                showToast(it)
            }
        }

        viewModel.doctorsListStateFlow.onEach {
            adapter.submitList(it)
        }.launchIn(lifecycleScope)


        lifecycleScope.launch {
            viewModel.loadingStateFlow.collect {
                binding.loadingPb.isVisible = it
            }
        }
        adapter.setOnCLickListener { data, image ->

            val extras = FragmentNavigatorExtras(image to "shared_element")

            findNavController().navigate(
                DoctorsFragmentDirections.actionDoctorsFragmentToAdminDoctorFragment(
                    data
                ), extras
            )
        }

    }

}