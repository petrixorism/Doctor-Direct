package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.databinding.FragmentProfileBinding
import uz.ravshanbaxranov.doctordirect.other.Constants
import uz.ravshanbaxranov.doctordirect.other.Permission
import uz.ravshanbaxranov.doctordirect.other.makeTwoDigit
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.ProfileViewModel
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var userData = User()
    private var timeRange: Pair<String, String> = Pair("", "")
    private var imageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentProfileBinding.bind(view)


        binding.imageIv.setOnClickListener {
            if (!Permission.hasLocationPermission(requireContext())) {
                Permission.requestPermission(this)
            } else {

                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, Constants.REQUEST_CODE_IMAGE_PICK)
            }
        }

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
            viewModel.updatedFlow.collect {
                imageUri = null
                showToast(getString(R.string.your_details_updated))
            }
        }

        lifecycleScope.launch {
            viewModel.signOutFlow.collect {
                parentFragment?.findNavController()?.navigate(R.id.action_loginFragment)
            }
        }

        lifecycleScope.launch {
            viewModel.userDetailsStateFlow.collect {

                userData = it

                binding.firstnameTv.setText(it.firstName)
                binding.lastnameTv.setText(it.lastName)
                binding.usernameTv.text = it.username
                binding.emailTv.setText(it.email)
                binding.passwordTv.setText(it.password)
                binding.maleRb.isChecked = it.male
                binding.femaleRb.isChecked = !it.male

                if (it.role == "doctor") {

                    binding.availableTimeTtv.isVisible = true
                    binding.aboutTv.isVisible = true
                    binding.specialityTv.isVisible = true
                    binding.availableTimeTtv.text = it.availableTime
                    binding.aboutTv.setText(it.bio)
                    binding.specialityTv.setText(it.speciality)
                }

                Glide.with(binding.imageIv)
                    .load(it.avatarUrl)
                    .placeholder(R.drawable.img_1)
                    .into(binding.imageIv)


            }
        }

        binding.updateBtn.setOnClickListener {
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
                role = "doctor",
                avatarUrl = userData.avatarUrl
            )

            viewModel.updateProfile(
                user = user,
                fileUri = imageUri
            )
        }

        binding.settingsBtn.setOnClickListener {
            showSettingsPopUp()
        }

        binding.usernameTv.setOnClickListener {
            showToast(getString(R.string.you_cant_change_username))
        }
        binding.availableTimeTtv.setOnClickListener {
            showTimeRangeDialog()
        }
    }

    private fun showTimeRangeDialog() {
        val startTimePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->

                timeRange = Pair(
                    first = "${makeTwoDigit(hourOfDay)}:${makeTwoDigit(minute)}",
                    second = timeRange.second
                )

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
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_IMAGE_PICK) {
            data?.data.let {
                try {
                    val path = getRealPathFromURI(it, requireActivity())
                    if (path != null) {
                        val photoFile = File(path)

                        imageUri = photoFile.toUri()

                        lifecycleScope.launch {

                            val compressedImageFile =
                                Compressor.compress(requireContext(), photoFile)
                            imageUri = compressedImageFile.toUri()

                            Glide.with(requireContext())
                                .load(imageUri)
                                .placeholder(R.drawable.image_placeholder)
                                .into(binding.imageIv)

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

    private fun showSettingsPopUp() {
        val popup = PopupMenu(requireContext(), binding.settingsBtn)
        popup.inflate(R.menu.doctor_settings_menu)
        popup.menu[0].isVisible = userData.role == "doctor"
        popup.menu[0].isCheckable = true
        popup.menu[0].isChecked = userData.available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }
        popup.show()


        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOut -> {

                        viewModel.signOut()



                    return@setOnMenuItemClickListener true
                }

                R.id.isAvailable -> {
                    popup.menu[0].isChecked = !popup.menu[0].isChecked
                    viewModel.changeDoctorAvailability(popup.menu[0].isChecked)

                }

                R.id.russian -> {
                    viewModel.changeLang("ru")
                    return@setOnMenuItemClickListener true
                }

                R.id.english -> {
                    viewModel.changeLang("en")
                    return@setOnMenuItemClickListener true
                }
            }

            return@setOnMenuItemClickListener true
        }
    }


}