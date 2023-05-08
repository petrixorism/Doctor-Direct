package uz.ravshanbaxranov.doctordirect.data.model

sealed class MainResult<T> {
    data class Success<T>(val data: T) : MainResult<T>()
    data class Message<T>(val message: String) : MainResult<T>()
    data class Loading<T>(val isLoading: Boolean) : MainResult<T>()
}
