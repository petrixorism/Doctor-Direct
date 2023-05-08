package uz.ravshanbaxranov.doctordirect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.ravshanbaxranov.doctordirect.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class DoctorHomeViewModel @Inject constructor(
    private val repository: UserRepository
) :ViewModel() {


}