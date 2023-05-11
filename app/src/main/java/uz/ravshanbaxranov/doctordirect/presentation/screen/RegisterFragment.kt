package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.databinding.FragmentRegisterBinding
import uz.ravshanbaxranov.doctordirect.other.Constants.REQUEST_CODE_IMAGE_PICK
import uz.ravshanbaxranov.doctordirect.other.Permission
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.RegisterViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val viewModel: RegisterViewModel by viewModels()
    private var imageUrl: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                binding.registerBtn.isEnabled = !it
                binding.loadingPb.isVisible = it
            }
        }

        lifecycleScope.launch {
            viewModel.successFlow.collect {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToUserNavigation())
            }
        }


        binding.birthDateTet.setOnClickListener {
            showDatePickerDialog()
        }

        binding.loginTv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.registerBtn.setOnClickListener {
            val user = User(
                firstName = binding.firstNameEt.text.toString(),
                lastName = binding.lastNameEt.text.toString(),
                password = binding.passwordTet.text.toString(),
                email = binding.mailEt.text.toString(),
                male = binding.maleRb.isChecked,
                birthDate = binding.birthDateTet.text.toString(),
                username = binding.usernameEt.text.toString()
            )
            viewModel.registerUser(user, imageUrl)
        }

        binding.avatarIv.setOnClickListener {
            if (!Permission.hasLocationPermission(requireContext())) {
                Permission.requestPermission(this)
            } else {

                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, REQUEST_CODE_IMAGE_PICK)
            }
        }


    }

    @SuppressLint("ResourceType")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data.let {
                try {

                    val path = getRealPathFromURI(it, requireActivity())
                    if (path != null) {
                        val photoFile = File(path)

                        imageUrl = photoFile.toUri()

                        lifecycleScope.launch {

                            val compressedImageFile =
                                Compressor.compress(requireContext(), photoFile)
                            imageUrl = compressedImageFile.toUri()

                            Glide.with(requireContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.image_placeholder)
                                .into(binding.avatarIv)

                        }

                    } else {
                        showToast(getString(R.string.retry))
                    }
                } catch (e: Throwable) {
                    showToast(e.message.toString())
                }
            }

        }
    }

    private fun getRealPathFromURI(contentURI: Uri?, context: Activity): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.managedQuery(
            contentURI, projection, null, null, null
        ) ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        return if (cursor.moveToFirst()) cursor.getString(columnIndex) else null
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the date selection
                val birthday = getFormattedBirthday(selectedYear, selectedMonth, selectedDay)
                binding.birthDateTet.setText(birthday)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun getFormattedBirthday(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


}