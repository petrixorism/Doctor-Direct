package uz.ravshanbaxranov.doctordirect.presentation.viewmodel

import android.net.Uri
import android.util.Patterns
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
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
import uz.ravshanbaxranov.doctordirect.domain.repository.AuthRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.GeneralRepository
import uz.ravshanbaxranov.doctordirect.other.Constants.LANGUAGE
import uz.ravshanbaxranov.doctordirect.other.showLog
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val generalRepository: GeneralRepository,
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {


    private val _updatedChannel = Channel<Unit>()
    val updatedFlow: Flow<Unit> = _updatedChannel.receiveAsFlow()

    private val _signOutChannel = Channel<Unit>()
    val signOutFlow: Flow<Unit> = _signOutChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()

    private val _userDetailsState = MutableStateFlow(User())
    val userDetailsStateFlow: Flow<User> = _userDetailsState.asStateFlow()


    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }


    init {
        viewModelScope.launch {
            generalRepository.getUserData().collect {
                when (it) {
                    is MainResult.Success -> {
                        _userDetailsState.emit(it.data ?: User())
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

    fun updateProfile(user: User, fileUri: Uri?) {
        viewModelScope.launch {
            if (user.firstName.isBlank()) {
                _errorChannel.send("Please enter your first name")
            } else if (user.lastName.isBlank()) {
                _errorChannel.send("Please enter your last name")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                _errorChannel.send("Please enter your email correctly")
            } else if (user.password.length < 6) {
                _errorChannel.send("Password must be at least 6 characters long")
            } else if (user.availableTime.isBlank() && user.role == "doctor") {
                _errorChannel.send("Please enter available time")
            } else if (user.speciality.isBlank() && user.role == "doctor") {
                _errorChannel.send("Please enter speciality")
            } else if (user.bio.isBlank() && user.role == "doctor") {
                _errorChannel.send("Please enter bio")
            } else generalRepository.updateProfile(user, fileUri).collect {
                showLog(fileUri.toString())
                when (it) {
                    is MainResult.Success -> {
                        _updatedChannel.send(Unit)
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

    fun changeLang(language: String) {
        viewModelScope.launch {
            dataStore.edit {
                it[LANGUAGE] = language
            }
        }
    }

    fun changeDoctorAvailability(checked: Boolean) {
        viewModelScope.launch {
            generalRepository.changeDoctorAvailability(checked)
        }
    }

}