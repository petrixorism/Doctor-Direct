package uz.ravshanbaxranov.doctordirect.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.data.model.remote.User

interface AdminRepository {

    suspend fun createDoctor(userData: User, fileUri: Uri): Flow<MainResult<Unit>>

    suspend fun deleteDoctor(username: String): Flow<MainResult<Unit>>

    suspend fun getAllDoctors(): Flow<MainResult<List<User>>>

    suspend fun getAllAppointments(): Flow<MainResult<List<Appointment>>>


}