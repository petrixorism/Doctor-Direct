package uz.ravshanbaxranov.doctordirect.other

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment

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

fun String.getId(): String? {
    return if (this.contains("Id")) {
        this.substring(4, indexOf("\n"))
    } else null
}