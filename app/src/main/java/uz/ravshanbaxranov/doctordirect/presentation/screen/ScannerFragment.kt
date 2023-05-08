package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentScannerBinding
import uz.ravshanbaxranov.doctordirect.other.Constants

class ScannerFragment : Fragment(R.layout.fragment_scanner) {

    private lateinit var codeScanner: CodeScanner
    private val binding by viewBinding(FragmentScannerBinding::bind)

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

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Scan result: ${it.text}", Toast.LENGTH_LONG)
                    .show()
            }

        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS

            Toast.makeText(
                requireContext(), "Camera initialization error: ${it.message}", Toast.LENGTH_LONG
            ).show()

        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
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