package uz.ravshanbaxranov.doctordirect.presentation.screen.doctor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.presentation.adapter.AppointmentAdapter
import uz.ravshanbaxranov.doctordirect.presentation.viewmodel.DoctorHomeViewModel

@AndroidEntryPoint
class DoctorHomeFragment : Fragment(R.layout.fragment_doctor_home) {

    private val adapter by lazy { AppointmentAdapter("doctor") }
    private val viewModel: DoctorHomeViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }


}