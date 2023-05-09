package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentScannerResultBinding
import java.text.SimpleDateFormat
import java.util.Date

class ScannerResultFragment : Fragment(R.layout.fragment_scanner_result) {

    private val binding by viewBinding(FragmentScannerResultBinding::bind)
    private val args by navArgs<ScannerResultFragmentArgs>()

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val appointment = args.appointment

        val time = SimpleDateFormat("HH:mm dd MMMM").format(Date(appointment.date))

        binding.patientNameTv.text = appointment.patient
        binding.aimTv.text = appointment.aim
        binding.dateTv.text = "Sent in $time"
        binding.arrivalTv.text = appointment.arrivalDate
        binding.diagnosisTv.text = appointment.diagnosis
        binding.recipeTv.text = appointment.recipe
        binding.conclusionTv.text = appointment.conclusion

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

    }

}