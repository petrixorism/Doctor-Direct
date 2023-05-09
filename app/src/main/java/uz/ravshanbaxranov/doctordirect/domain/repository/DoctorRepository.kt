package uz.ravshanbaxranov.doctordirect.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.data.model.remote.User

interface DoctorRepository {

    suspend fun updateAppointment(appointment: Appointment): Flow<MainResult<Unit>>

    suspend fun getDoctorAppointments(): Flow<MainResult<List<Appointment>>>

}