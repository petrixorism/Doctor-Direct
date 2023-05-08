package uz.ravshanbaxranov.doctordirect.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.data.model.remote.User

interface UserRepository {

    suspend fun getDoctors(): Flow<MainResult<List<User>>>

    suspend fun getUserData(): Flow<MainResult<User?>>

    suspend fun sendAppointment(appointment: Appointment): Flow<MainResult<Unit>>

    suspend fun getUserAppointments(): Flow<MainResult<List<Appointment>>>
}