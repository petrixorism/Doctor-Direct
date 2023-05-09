package uz.ravshanbaxranov.doctordirect.presentation.viewmodel.admin

import android.net.Uri
import android.util.Patterns
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
import javax.inject.Inject

@HiltViewModel
class AddDoctorViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {


    private val _successChannel = Channel<Unit>()
    val successFlow: Flow<Unit> = _successChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    fun addDoctor(user: User, imageUri: Uri?) {
        viewModelScope.launch {
            if (user.firstName.isBlank()) {
                _errorChannel.send("Please enter your first name")
            } else if (user.lastName.isBlank()) {
                _errorChannel.send("Please enter your last name")
            } else if (user.username.length < 3) {
                _errorChannel.send("Username must be at least 3 characters long")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                _errorChannel.send("Please enter your email correctly")
            } else if (user.password.length < 6) {
                _errorChannel.send("Password must be at least 6 characters long")
            }  else if (user.availableTime.isBlank()) {
                _errorChannel.send("Please enter available time")
            } else if (user.speciality.isBlank()) {
                _errorChannel.send("Please enter speciality")
            }  else if (user.bio.isBlank()) {
                _errorChannel.send("Please enter bio")
            } else if (imageUri == null) {
                _errorChannel.send("Please pick your image")
            } else adminRepository.createDoctor(user, imageUri).collect {
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