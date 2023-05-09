package uz.ravshanbaxranov.doctordirect.presentation.screen.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentDoctorBinding
import uz.ravshanbaxranov.doctordirect.other.showLog

class DoctorFragment : Fragment(R.layout.fragment_doctor) {

    private val binding by viewBinding(FragmentDoctorBinding::bind)
    private val args by navArgs<DoctorFragmentArgs>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val doctor = args.doctor

        Glide.with(binding.avatarIv)
            .load(doctor.avatarUrl)
            .placeholder(R.drawable.img_1)
            .into(binding.avatarIv)

        binding.fullNameTv.text = "${doctor.firstName} ${doctor.lastName}"
        binding.aboutTv.text = doctor.bio
        binding.specialityTv.text = doctor.speciality
        binding.availabilityTv.text = doctor.availableTime

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation

        binding.appointmentBtn.setOnClickListener {

                findNavController().navigate(
                    DoctorFragmentDirections.actionDoctorFragmentToAppointmentFragment(
                        args.doctor,
                        args.username,
                        args.fullname
                    )
                )

        }


    }

}