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
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.domain.repository.DoctorRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.GeneralRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class DoctorHomeViewModel @Inject constructor(
    private val repository: DoctorRepository,
    private val generalRepository: GeneralRepository,
) :ViewModel() {


    private val _doctorsListStateFlow = MutableStateFlow<List<Appointment>>(emptyList())
    val doctorsListStateFlow = _doctorsListStateFlow.asStateFlow()

    private val _userDataStateFlow = MutableStateFlow(User())
    val userDataStateFlow = _userDataStateFlow.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    init {
        viewModelScope.launch {
            repository.getDoctorAppointments().collect {
                when (it) {
                    is MainResult.Success -> {
                        _doctorsListStateFlow.emit(it.data)
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

        viewModelScope.launch {
            generalRepository.getUserData().collect {
                when (it) {
                    is MainResult.Success -> {
                        _userDataStateFlow.emit(it.data ?: User())
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