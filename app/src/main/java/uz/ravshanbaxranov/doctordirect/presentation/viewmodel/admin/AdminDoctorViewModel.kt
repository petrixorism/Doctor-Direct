package uz.ravshanbaxranov.doctordirect.presentation.viewmodel.admin

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
import uz.ravshanbaxranov.doctordirect.domain.repository.AdminRepository
import javax.inject.Inject

@HiltViewModel
class AdminDoctorViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {


    private val _successChannel = Channel<Unit>()
    val successFlow: Flow<Unit> = _successChannel.receiveAsFlow()

    private val _errorChannel = Channel<String>()
    val errorFlow: Flow<String> = _errorChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingStateFlow: Flow<Boolean> = _loadingState.asStateFlow()


    fun deleteDoctor(userName: String) {
        viewModelScope.launch {
            adminRepository.deleteDoctor(userName).collect {
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