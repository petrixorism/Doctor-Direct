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
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.domain.repository.AdminRepository
import uz.ravshanbaxranov.doctordirect.other.showLog
import javax.inject.Inject

@HiltViewModel
class DoctorsViewModel @Inject constructor(
    private val doctorRepository: AdminRepository
) : ViewModel() {

    private val _doctorsListStateFlow = MutableStateFlow<List<User>>(emptyList())
    val doctorsListStateFlow = _doctorsListStateFlow.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    init {
        viewModelScope.launch {
            doctorRepository.getAllDoctors().collect {
                showLog(it.toString())
                when (it) {
                    is MainResult.Success -> {
                        _doctorsListStateFlow.emit(
                            it.data.filter { user ->
                                user.role == "doctor"
                            }
                        )
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