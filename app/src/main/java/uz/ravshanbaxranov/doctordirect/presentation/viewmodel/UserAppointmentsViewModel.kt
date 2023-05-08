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
class UserAppointmentsViewModel @Inject constructor(
    private val repository: UserRepository
):ViewModel() {


    private val _successStateFlow = MutableStateFlow<List<Appointment>>(emptyList())
    val successFlow: Flow<List<Appointment>> = _successStateFlow.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    init {

        viewModelScope.launch {
            repository.getUserAppointments().collect{
                when (it) {
                    is MainResult.Success -> {
                        _successStateFlow.value = it.data
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