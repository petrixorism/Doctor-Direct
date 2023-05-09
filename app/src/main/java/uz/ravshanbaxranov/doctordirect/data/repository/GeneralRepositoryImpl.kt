package uz.ravshanbaxranov.doctordirect.data.repository

import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.domain.repository.GeneralRepository
import javax.inject.Inject

class GeneralRepositoryImpl @Inject constructor(
    private val fireStore: DocumentReference
) : GeneralRepository {

    override suspend fun getQrResult(id: String): Flow<MainResult<Appointment>> =
        callbackFlow<MainResult<Appointment>> {
            trySendBlocking(MainResult.Loading(true))


            val appointment = fireStore.collection("appointments").document(id).get().await()
                .toObject(Appointment::class.java)

            if (appointment == null) {
                trySendBlocking(MainResult.Message("Appointment not found"))
            } else {
                trySendBlocking(MainResult.Success(appointment))
            }

            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))
        }
}