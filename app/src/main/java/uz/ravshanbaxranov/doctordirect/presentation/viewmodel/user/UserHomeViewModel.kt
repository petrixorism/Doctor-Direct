package uz.ravshanbaxranov.doctordirect.presentation.viewmodel.user

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
import uz.ravshanbaxranov.doctordirect.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _doctorsListStateFlow = MutableStateFlow<List<User>>(emptyList())
    val doctorsListStateFlow = _doctorsListStateFlow.asStateFlow()

    private val _userDataStateFlow = MutableStateFlow(User())
    val userDataStateFlow = _userDataStateFlow.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    init {
        viewModelScope.launch {
            userRepository.getDoctors().collect {
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
            userRepository.getUserData().collect {
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