package uz.ravshanbaxranov.doctordirect.presentation.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.domain.repository.AuthRepository
import uz.ravshanbaxranov.doctordirect.other.Constants
import uz.ravshanbaxranov.doctordirect.other.showLog
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _successChannel = Channel<String>()
    val successFlow: Flow<String> = _successChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()

    private val _userScreenChannel = Channel<Unit>()
    val userScreenFlow: Flow<Unit> = _userScreenChannel.receiveAsFlow()

    private val _doctorScreenChannel = Channel<Unit>()
    val doctorScreenFlow: Flow<Unit> = _doctorScreenChannel.receiveAsFlow()

    private val _adminScreenChannel = Channel<Unit>()
    val adminScreenFlow: Flow<Unit> = _adminScreenChannel.receiveAsFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authRepository.loginUser(username, password).collect {
                showLog(it.toString())
                when (it) {
                    is MainResult.Success -> {
                        when (it.data) {
                            "admin" -> {
                                _adminScreenChannel.send(Unit)
                            }
                            "doctor" -> {
                                _doctorScreenChannel.send(Unit)
                            }
                            "user" -> {
                                _userScreenChannel.send(Unit)
                            }
                        }

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