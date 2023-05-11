package uz.ravshanbaxranov.doctordirect.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.domain.repository.DoctorRepository
import uz.ravshanbaxranov.doctordirect.other.Constants
import javax.inject.Inject

class DoctorRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val fireStore: DocumentReference
) : DoctorRepository {

    override suspend fun updateAppointment(appointment: Appointment): Flow<MainResult<Unit>> =
        callbackFlow<MainResult<Unit>> {
            trySendBlocking(MainResult.Loading(true))

            if (appointment.status == 2) {
                fireStore.collection("appointments").document(appointment.id).update(
                    "status", appointment.status
                ).addOnSuccessListener {
                    trySendBlocking(MainResult.Loading(false))
                    trySendBlocking(MainResult.Success(Unit))
                }
            } else {
                fireStore.collection("appointments").document(appointment.id).update(
                    "status", appointment.status,
                    "diagnosis", appointment.diagnosis,
                    "recipe", appointment.recipe,
                    "conclusion", appointment.conclusion
                ).addOnSuccessListener {
                    trySendBlocking(MainResult.Loading(false))
                    trySendBlocking(MainResult.Success(Unit))
                }
            }



            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))

        }

    override suspend fun getDoctorAppointments(): Flow<MainResult<List<Appointment>>> =
        callbackFlow<MainResult<List<Appointment>>> {
            trySendBlocking(MainResult.Loading(true))

            dataStore.data.collect { preferences ->
                val username = preferences[Constants.USERNAME] ?: ""

                fireStore.collection("appointments").addSnapshotListener { value, e ->
                    val users = ArrayList<Appointment>()
                    for (i in value!!.documents) {
                        val appointment = i.toObject(Appointment::class.java)!!
                        if (appointment.doctorUsername == username)
                            users.add(appointment)
                    }

                    if (e != null) {
                        trySendBlocking(MainResult.Message(e.message.toString()))
                    } else {
                        trySendBlocking(MainResult.Success(users))
                    }
                }
                trySendBlocking(MainResult.Loading(false))
            }
            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))
        }
}