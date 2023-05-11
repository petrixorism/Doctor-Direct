package uz.ravshanbaxranov.mytaxi.presentation.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.domain.repository.AuthRepository
import uz.ravshanbaxranov.doctordirect.other.Constants.IS_FIRST_TIME
import uz.ravshanbaxranov.doctordirect.other.Constants.IS_LOGGED_IN
import uz.ravshanbaxranov.doctordirect.other.Constants.USERNAME
import uz.ravshanbaxranov.doctordirect.other.showLog
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _onboardingChannel = Channel<Unit>()
    val onboardingFlow: Flow<Unit> = _onboardingChannel.receiveAsFlow()

    private val _userScreenChannel = Channel<Unit>()
    val userScreenFlow: Flow<Unit> = _userScreenChannel.receiveAsFlow()

    private val _doctorScreenChannel = Channel<Unit>()
    val doctorScreenFlow: Flow<Unit> = _doctorScreenChannel.receiveAsFlow()

    private val _adminScreenChannel = Channel<Unit>()
    val adminScreenFlow: Flow<Unit> = _adminScreenChannel.receiveAsFlow()

    private val _loginScreenChannel = Channel<Unit>()
    val loginScreenFlow: Flow<Unit> = _loginScreenChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            delay(2000L)
            dataStore.data.collect { preferences ->
                val isFirstTime = preferences[IS_FIRST_TIME] ?: true
                val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
                val username = preferences[USERNAME] ?: ""

                if (isFirstTime) {
                    _onboardingChannel.send(Unit)
                } else {
                    if (isLoggedIn) {
                        authRepository.getUserData(username).collect { response ->
                            when (response) {
                                is MainResult.Success -> {
                                    when (response.data?.role) {
                                        null -> {
                                            dataStore.edit {
                                                it[IS_LOGGED_IN] = false
                                            }
                                        }
                                        "admin" -> {
                                            _adminScreenChannel.send(Unit)
                                        }
                                        "doctor" -> {
                                            _doctorScreenChannel.send(Unit)
                                        }
                                        else -> {
                                            _userScreenChannel.send(Unit)
                                        }
                                    }
                                }
                                is MainResult.Loading -> {
                                    _loadingState.value = response.isLoading
                                }
                                is MainResult.Message -> {
                                    _loginScreenChannel.send(Unit)
                                }
                            }
                        }
                    }
                    else {
                        _loginScreenChannel.send(Unit)
                    }

                }
            }
        }
    }

}
