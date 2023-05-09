package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentScannerBinding
import uz.ravshanbaxranov.doctordirect.other.Constants
import uz.ravshanbaxranov.doctordirect.other.getId
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.ScannerViewModel

@AndroidEntryPoint
class ScannerFragment : Fragment(R.layout.fragment_scanner) {

    private lateinit var codeScanner: CodeScanner
    private val binding by viewBinding(FragmentScannerBinding::bind)
    private val viewModel: ScannerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(
                this,
                "This application cannot work without permission",
                Constants.PERMISSION_CAMERA_REQUEST_CODE,
                Manifest.permission.CAMERA
            )
        }


        codeScanner = CodeScanner(requireContext(), binding.scannerView)

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            lifecycleScope.launch(Dispatchers.Main) {

                val appointmentId = it.text.getId()

                if (appointmentId != null) {
                    viewModel.getAppointment(appointmentId)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "This is not Appointment QR",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

        }

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
                findNavController().navigate(ScannerFragmentDirections.actionScannerFragmentToScannerResultFragment(it))
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            Toast.makeText(requireContext(), "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
        }

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}