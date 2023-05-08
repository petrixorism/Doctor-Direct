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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.domain.repository.UserRepository
import uz.ravshanbaxranov.doctordirect.other.Constants.USERNAME
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val fireStore: DocumentReference,
    private val dataStore: DataStore<Preferences>
) : UserRepository {

    override suspend fun getDoctors(): Flow<MainResult<List<User>>> =
        callbackFlow<MainResult<List<User>>> {
            trySendBlocking(MainResult.Loading(true))

            fireStore.collection("users").addSnapshotListener { value, e ->
                val users = ArrayList<User>()
                for (i in value!!.documents) {
                    val userData = i.toObject(User::class.java)!!
                    if (userData.role == "doctor")
                        users.add(userData)
                }

                if (e != null) {
                    trySendBlocking(MainResult.Message(e.message.toString()))
                } else {
                    trySendBlocking(MainResult.Success(users))
                }
            }
            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))
        }

    override suspend fun getUserData(): Flow<MainResult<User?>> =
        callbackFlow<MainResult<User?>> {

            trySendBlocking(MainResult.Loading(true))

            withContext(Dispatchers.IO) {
                dataStore.data.collect {
                    val username = it[USERNAME] ?: ""

                    val user = fireStore.collection("users").document(username).get().await()
                        .toObject(User::class.java)

                    trySendBlocking(MainResult.Success(user))
                    trySendBlocking(MainResult.Loading(false))
                }
            }

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))

        }

    override suspend fun sendAppointment(appointment: Appointment): Flow<MainResult<Unit>> =
        callbackFlow<MainResult<Unit>> {

            trySendBlocking(MainResult.Loading(true))
            val id = fireStore.collection("id").document().id

            appointment.id = id

            val collection = fireStore.collection("appointments")
            dataStore.data.collect { preferences ->
                val username = preferences[USERNAME] ?: ""
                appointment.patientUsername = username
                collection.document(id).set(appointment).addOnSuccessListener {
                    trySendBlocking(MainResult.Success(Unit))
                }.addOnFailureListener {
                    trySendBlocking(MainResult.Message(it.message.toString()))
                }.addOnCanceledListener {
                    trySendBlocking(MainResult.Message("Rejected"))
                }.addOnCompleteListener {
                    trySendBlocking(MainResult.Loading(false))
                }
            }

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))
        }

    override suspend fun getUserAppointments(): Flow<MainResult<List<Appointment>>> =
        callbackFlow<MainResult<List<Appointment>>> {
            trySendBlocking(MainResult.Loading(true))

            dataStore.data.collect { preferences ->
                val username = preferences[USERNAME] ?: ""

                fireStore.collection("appointments").addSnapshotListener { value, e ->
                    val users = ArrayList<Appointment>()
                    for (i in value!!.documents) {
                        val appointment = i.toObject(Appointment::class.java)!!
                        if (appointment.patientUsername == username)
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
