package uz.ravshanbaxranov.doctordirect.presentation.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.doctordirect.other.Constants.LANGUAGE
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _changeLangToRussian = Channel<String>()
    val changeLangToRussianFlow = _changeLangToRussian.receiveAsFlow()

    init {
        viewModelScope.launch {
            dataStore.data.collect {
                val lang = it[LANGUAGE] ?: "en"
                _changeLangToRussian.send(lang)
            }
        }
    }
}


