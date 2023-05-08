package uz.ravshanbaxranov.doctordirect.presentation.viewmodel

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
import uz.ravshanbaxranov.doctordirect.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MakeAppointmentViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {


    private val _successChannel = Channel<Unit>()
    val successFlow: Flow<Unit> = _successChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    fun makeAppointment(appointment: Appointment) {
        viewModelScope.launch {
            if (appointment.arrivalDate.isBlank()) {
                _errorChannel.send("Enter arrival time")
            } else if (appointment.aim.isBlank()) {
                _errorChannel.send("Enter a brief aim")
            } else {
                repository.sendAppointment(appointment).collect {
                    when (it) {
                        is MainResult.Success -> {
                            _successChannel.send(Unit)
                        }

                        is MainResult.Loading -> {
                            _loadingState.value = it.isLoading
                        }

                        is MainResult.Message -> {
                            _errorChannel.send(it.message)
                        }
                    }
                }
            }

        }
    }

}