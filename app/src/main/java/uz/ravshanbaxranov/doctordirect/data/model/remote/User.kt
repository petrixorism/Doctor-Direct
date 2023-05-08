package uz.ravshanbaxranov.doctordirect.data.model.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val username: String = "",
    val email: String = "",
    var avatarUrl: String = "",
    val birthDate: String = "",
    val male: Boolean = true,
    val role: String = "user",
    val speciality: String = "",
    val bio: String = "",
    val availableTime: String = "",
    val available: Boolean = true
) : Parcelable {
    fun getFullName()= "$firstName $lastName"
}
