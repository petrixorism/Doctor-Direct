package uz.ravshanbaxranov.doctordirect.presentation.screen.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.databinding.FragmentAddDoctorBinding
import uz.ravshanbaxranov.doctordirect.other.Constants.REQUEST_CODE_IMAGE_PICK
import uz.ravshanbaxranov.doctordirect.other.Permission
import uz.ravshanbaxranov.doctordirect.other.makeTwoDigit
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.admin.AddDoctorViewModel
import java.io.File

@AndroidEntryPoint
class AddDoctorFragment : Fragment(R.layout.fragment_add_doctor) {

    private val binding by viewBinding(FragmentAddDoctorBinding::bind)
    private val viewModel: AddDoctorViewModel by viewModels()
    private var timeRange: Pair<String, String> = Pair("", "")
    private var imageUrl: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.errorFlow.onEach {
            showToast(it)
        }.launchIn(lifecycleScope)

        viewModel.loadingStateFlow.onEach {
            binding.loadingPb.isVisible = it
            binding.addBtn.isEnabled = !it
        }.launchIn(lifecycleScope)

        viewModel.successFlow.onEach {
            showToast("Doctor has been added")
        }.launchIn(lifecycleScope)

        binding.imageIv.setOnClickListener {
            if (!Permission.hasLocationPermission(requireContext())) {
                Permission.requestPermission(this)
            } else {

                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, REQUEST_CODE_IMAGE_PICK)
            }
        }
        binding.addBtn.setOnClickListener {
            val user = User(
                firstName = binding.firstnameTv.text.toString(),
                lastName = binding.lastnameTv.text.toString(),
                password = binding.passwordTv.text.toString(),
                email = binding.emailTv.text.toString(),
                male = binding.maleRb.isChecked,
                birthDate = "",
                username = binding.usernameTv.text.toString(),
                speciality = binding.specialityTv.text.toString(),
                bio = binding.aboutTv.text.toString(),
                availableTime = binding.availableTimeTtv.text.toString(),
                role = "doctor"
            )

            viewModel.addDoctor(
                user = user,
                imageUri = imageUrl
            )
        }

        binding.availableTimeTtv.setOnClickListener {
            showTimeRangeDialog()
        }
    }

    private fun showTimeRangeDialog() {
        val startTimePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->

                Pair("${makeTwoDigit(hourOfDay)}:${makeTwoDigit(minute)}", timeRange.second)

                showEndTimePicker()
            },
            0,
            0,
            true
        )
        startTimePicker.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showEndTimePicker() {
        val endTimePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                timeRange = if (minute < 10) {
                    Pair(timeRange.first, "$hourOfDay:0$minute")
                } else {
                    Pair(timeRange.first, "$hourOfDay:$minute")
                }
                binding.availableTimeTtv.text = "${timeRange.first} / ${timeRange.second}"
            },
            0,
            0,
            true
        )
        endTimePicker.show()
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
                                .into(binding.imageIv)

                        }

                    } else {
                        showToast("Retry")
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


}