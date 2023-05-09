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
import uz.ravshanbaxranov.doctordirect.domain.repository.GeneralRepository
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val generalRepository: GeneralRepository
) : ViewModel() {


    private val _successChannel = Channel<Appointment>()
    val successFlow: Flow<Appointment> = _successChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    fun getAppointment(id: String) {
        viewModelScope.launch {
            generalRepository.getQrResult(id).collect {
                when (it) {
                    is MainResult.Success -> {
                        _successChannel.send(it.data)
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