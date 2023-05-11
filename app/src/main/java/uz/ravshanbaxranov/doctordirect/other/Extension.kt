package uz.ravshanbaxranov.doctordirect.other

import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun showLog(message: String) {
    Log.d("TAGDF", message)
}

fun makeTwoDigit(digit: Int): String {
    return if (digit < 10) "0$digit"
    else "$digit"
}

fun String.getIdForAppointment(): String? {
    return if (this.contains("Id")) {
        this.substring(4, indexOf("\n"))
    } else null
}
fun String.getIdForUser(): String? {
    showLog(this.substring(6, indexOf("\n")))
    return if (this.contains("uid")) {
        this.substring(6, indexOf("\n"))

    } else null
}

fun getStringFromId(id: Int): String =
    Resources.getSystem().getString(id)

fun Fragment.getTextDataForQR(appointment: Appointment): String {
    val text = StringBuilder()

    appointment.apply {
        text.append("Id: $id\n")
        text.append("${getString(R.string.doctor)}: $doctor\n")
        text.append("${getString(R.string.patient)}: $patient\n")
        text.append("${getString(R.string.diagnosis)}: $diagnosis\n")
        text.append("${getString(R.string.recipe)}: $recipe\n")
        text.append("${getString(R.string.conclusion)}: $conclusion\n")
        text.append("${getString(R.string.arrival_date)}: $arrivalDate\n")
    }


    return text.toString()
}