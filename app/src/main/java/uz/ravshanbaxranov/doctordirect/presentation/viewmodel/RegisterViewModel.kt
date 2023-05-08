package uz.ravshanbaxranov.doctordirect.presentation.viewmodel

import android.net.Uri
import android.util.Patterns
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
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.domain.repository.AuthRepository
import uz.ravshanbaxranov.doctordirect.other.Constants.IS_LOGGED_IN
import uz.ravshanbaxranov.doctordirect.other.Constants.USERNAME
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {


    private val _successChannel = Channel<Unit>()
    val successFlow: Flow<Unit> = _successChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    fun registerUser(user: User, fileUri: Uri?) {


        viewModelScope.launch() {
            if (fileUri.toString().isBlank()) {
                _errorChannel.send("Please select your image")
            } else if (user.firstName.isBlank()) {
                _errorChannel.send("Please enter your first name")
            } else if (user.lastName.isBlank()) {
                _errorChannel.send("Please enter your last name")
            } else if (user.username.length < 3) {
                _errorChannel.send("Username must be at least 3 characters long")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                _errorChannel.send("Please enter your email correctly")
            } else if (user.password.length < 6) {
                _errorChannel.send("Password must be at least 6 characters long")
            } else if (user.birthDate.isBlank()) {
                _errorChannel.send("Please enter birth date")
            } else if (fileUri == null) {
                _errorChannel.send("Please pick your image")
            } else {
                authRepository.createUserData(user, fileUri).collect {
                    when (it) {
                        is MainResult.Success -> {
                            _successChannel.send(Unit)
                            launch(Dispatchers.IO) {
                                dataStore.edit { preferences ->
                                    preferences[IS_LOGGED_IN] = true
                                    preferences[USERNAME] = user.username
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

}