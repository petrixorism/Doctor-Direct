package uz.ravshanbaxranov.doctordirect.presentation.screen.user

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.databinding.FragmentMakeAppointmentBinding
import uz.ravshanbaxranov.doctordirect.other.makeTwoDigit
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.user.MakeAppointmentViewModel
import java.util.Calendar

@AndroidEntryPoint
class AppointmentFragment : Fragment(R.layout.fragment_make_appointment) {

    private lateinit var binding : FragmentMakeAppointmentBinding
    private val viewModel: MakeAppointmentViewModel by viewModels()
    private val args by navArgs<AppointmentFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMakeAppointmentBinding.bind(view)

        val doctorData = args.doctor

        binding.arrivalTimeTv.setOnClickListener {
            openDateTimePicker(requireContext())
        }



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
            viewModel.successFlow.collect {
                hideAnimation()
                showAnimation()
            }
        }

        binding.appointmentBtn.setOnClickListener {

            val appointment = Appointment(
                patient = args.fullname,
                doctor = doctorData.getFullName(),
                speciality = doctorData.speciality,
                aim = binding.aimTv.text.toString(),
                patientUsername = args.username,
                doctorUsername = doctorData.username,
                arrivalDate = binding.arrivalTimeTv.text.toString()
            )

            viewModel.makeAppointment(appointment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun showAnimation() {
        binding.textSuccess.isVisible = true
        binding.textSubsuccess.isVisible = true
        binding.lottieAnimationView.isVisible = true
        binding.lottieAnimationView.playAnimation()
        ObjectAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                binding.textSuccess.alpha = it.animatedValue as Float
                binding.textSubsuccess.alpha = it.animatedValue as Float
                binding.lottieAnimationView.alpha = it.animatedValue as Float
            }
            duration = 1000L
            start()
        }
    }

    private fun hideAnimation() {
        ObjectAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                binding.textTime.alpha = it.animatedValue as Float
                binding.arrivalTimeTv.alpha = it.animatedValue as Float
                binding.aimTv.alpha = it.animatedValue as Float
                binding.textAim.alpha = it.animatedValue as Float
                binding.appointmentBtn.alpha = it.animatedValue as Float
            }
            duration = 1000L
            start()
        }
        lifecycleScope.launch {
            delay(1000L)
            binding.textTime.isVisible = false
            binding.arrivalTimeTv.isVisible = false
            binding.aimTv.isVisible = false
            binding.textAim.isVisible = false
            binding.appointmentBtn.isVisible = false
        }

    }

    private fun openDateTimePicker(context: Context) {
        // Get Current Date
        val calendar: Calendar = Calendar.getInstance()
        val mYear = calendar.get(Calendar.YEAR)
        val mMonth = calendar.get(Calendar.MONTH)
        val mDay = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            context, { view, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth/${monthOfYear + 1}/$year"
                openTimePicker(context, date)
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun openTimePicker(context: Context, date: String) {

        // Get Current Time
        val c: Calendar = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(
            context, { _, hourOfDay, minute ->
                val arrivalTime = "${makeTwoDigit(hourOfDay)}:${makeTwoDigit(minute)} $date"
                binding.arrivalTimeTv.text = arrivalTime
            },
            mHour,
            mMinute,
            true
        )
        timePickerDialog.show()
    }

}