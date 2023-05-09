package uz.ravshanbaxranov.doctordirect.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment

interface GeneralRepository {

    suspend fun getQrResult(id: String): Flow<MainResult<Appointment>>

}