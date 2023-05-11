package uz.ravshanbaxranov.doctordirect.presentation.screen.doctor

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentReviewAppointmentBinding
import uz.ravshanbaxranov.doctordirect.other.showToast
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.doctor.ReviewAppointmentViewModel
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class ReviewAppointmentFragment : Fragment(R.layout.fragment_review_appointment) {

    private val binding by viewBinding(FragmentReviewAppointmentBinding::bind)
    private val viewModel: ReviewAppointmentViewModel by viewModels()
    private val args by navArgs<ReviewAppointmentFragmentArgs>()


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val appointment = args.appointment

        val time = SimpleDateFormat("HH:mm dd MMMM").format(Date(appointment.date))

        binding.patientNameTv.text = appointment.patient
        binding.aimTv.text = appointment.aim
        binding.dateTv.append(time)
        binding.arrivalTv.text = appointment.arrivalDate
        binding.diagnosisTv.setText(appointment.diagnosis)
        binding.recipeTv.setText(appointment.recipe)
        binding.conclusionTv.setText(appointment.conclusion)

        viewModel.errorFlow.onEach {
            val msg = it.toIntOrNull()
            if (msg==null){
                showToast(it)
            } else{
                showToast(getString(msg))
            }
        }.launchIn(lifecycleScope)

        lifecycleScope.launch {
            viewModel.loadingStateFlow.collect {
                binding.loadingPb.isVisible = it
            }
        }

        lifecycleScope.launch {
            viewModel.successFlow.collect {
                if (it == 1) {
                    onAccepted()
                } else {
                    onRejected()
                }
                showAnimation()
                hideAnimation()
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rejectBtn.setOnClickListener {
            viewModel.updateAppointment(appointment.copy(status = 2))
        }
        binding.acceptBtn.setOnClickListener {
            viewModel.updateAppointment(
                appointment.copy(
                    status = 1,
                    diagnosis = binding.diagnosisTv.text.toString(),
                    recipe = binding.recipeTv.text.toString(),
                    conclusion = binding.conclusionTv.text.toString(),
                )
            )
        }

    }

    private fun showAnimation() {
        binding.textSuccess.isVisible = true
        binding.textSubtext.isVisible = true
        binding.lottieAnimationView.isVisible = true
        binding.lottieAnimationView.playAnimation()
        ObjectAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                binding.textSuccess.alpha = it.animatedValue as Float
                binding.textSubtext.alpha = it.animatedValue as Float
                binding.lottieAnimationView.alpha = it.animatedValue as Float
            }
            duration = 1000L
            start()
        }
    }

    private fun hideAnimation() {
        ObjectAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                binding.scrollContainer.children.forEach { view ->
                    view.alpha = it.animatedValue as Float
                }

                binding.rejectBtn.alpha = it.animatedValue as Float
                binding.acceptBtn.alpha = it.animatedValue as Float
            }
            duration = 1000L
            start()
        }
        lifecycleScope.launch {
            delay(1000L)
            binding.scrollContainer.isVisible = false
            binding.acceptBtn.isVisible = false
            binding.rejectBtn.isVisible = false
        }

    }

    private fun onRejected() {
        binding.lottieAnimationView.setAnimation(R.raw.cancel_event)
        binding.textSuccess.setText(R.string.rejected)
        binding.textSubtext.setText(R.string.appointment_has_been_rejected)
    }

    private fun onAccepted() {
        binding.lottieAnimationView.setAnimation(R.raw.success)
        binding.textSuccess.setText(R.string.accepted)
        binding.textSubtext.setText(R.string.appointment_has_been_accepted)
    }

}