package uz.ravshanbaxranov.doctordirect.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.data.model.remote.User

interface GeneralRepository {

    suspend fun getQrResult(id: String): Flow<MainResult<Appointment>>

    suspend fun getUserData(): Flow<MainResult<User?>>

    suspend fun updateProfile(user: User, fileUri: Uri?): Flow<MainResult<Unit>>

    suspend fun changeDoctorAvailability(checked: Boolean)
}