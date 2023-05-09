package uz.ravshanbaxranov.doctordirect.presentation.viewmodel.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.domain.repository.DoctorRepository
import javax.inject.Inject

@HiltViewModel
class ReviewAppointmentViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
) : ViewModel() {


    private val _successChannel = Channel<Int>()
    val successFlow: Flow<Int> = _successChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            if (appointment.status == 1 && appointment.diagnosis.isBlank()) {
                _errorChannel.send("Fill diagnosis")
            } else if (appointment.status == 1 && appointment.recipe.isBlank()) {
                _errorChannel.send("Fill recipe")
            } else if (appointment.status == 1 && appointment.conclusion.isBlank()) {
                _errorChannel.send("Fill Conclusion")
            } else {
                doctorRepository.updateAppointment(appointment).collect {
                    when (it) {
                        is MainResult.Success -> {
                            _successChannel.send(appointment.status)
                        }

                        is MainResult.Message -> {
                            _errorChannel.send(it.message)
                        }

                        is MainResult.Loading -> {
                            _loadingState.value = it.isLoading
                        }
                    }
                }
            }
        }
    }

}