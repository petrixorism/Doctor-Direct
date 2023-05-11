package uz.ravshanbaxranov.doctordirect.data.model.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import uz.ravshanbaxranov.doctordirect.R

@Parcelize
data class Appointment(
    var id: String = "",
    val patient: String = "",
    val doctor: String = "",
    val speciality: String = "",
    val diagnosis: String = "",
    val aim: String = "",
    val recipe: String = "",
    val conclusion: String = "",
    var patientUsername: String = "",
    val doctorUsername: String = "",
    // 0 - UPCOMING
    // 1 - ACCEPTED
    // 2 - REJECTED
    val status: Int = 0,
    val arrivalDate: String = "",
    val date: Long = System.currentTimeMillis()
) : Parcelable {


}
