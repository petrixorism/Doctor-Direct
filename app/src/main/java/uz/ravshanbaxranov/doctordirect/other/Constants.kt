package uz.ravshanbaxranov.doctordirect.other

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {

    val IS_FIRST_TIME = booleanPreferencesKey("IS_FIRST_TIME")
    val IS_LOGGED_IN = booleanPreferencesKey("IS_LOGGED_IN")
    val USERNAME = stringPreferencesKey("USERNAME")

    const val PERMISSION_STORAGE_REQUEST_CODE = 123
    const val PERMISSION_CAMERA_REQUEST_CODE = 125
    const val REQUEST_CODE_IMAGE_PICK = 124

}