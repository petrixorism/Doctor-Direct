package uz.ravshanbaxranov.doctordirect.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
import uz.ravshanbaxranov.doctordirect.domain.repository.GeneralRepository
import uz.ravshanbaxranov.doctordirect.other.Constants.USERNAME
import javax.inject.Inject

class GeneralRepositoryImpl @Inject constructor(
    private val fireStore: DocumentReference,
    private val dataStore: DataStore<Preferences>
) : GeneralRepository {

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

    override suspend fun getUserDataFromUsername(username: String): Flow<MainResult<User>> =
        callbackFlow<MainResult<User>> {

            trySendBlocking(MainResult.Loading(true))

            val user = fireStore.collection("users").document(username.trim()).get().await()
                .toObject(User::class.java)

            if (user == null) {
                trySendBlocking(MainResult.Message("Appointment not found"))
            } else {
                trySendBlocking(MainResult.Success(user))
            }

            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))

        }

    override suspend fun updateProfile(user: User, fileUri: Uri?): Flow<MainResult<Unit>> =
        callbackFlow<MainResult<Unit>> {

            trySendBlocking(MainResult.Loading(true))

            val cloudStorage = Firebase.storage.reference

            val idImage = fireStore.collection("id").document().id


            val imageRef = cloudStorage.child("images/$idImage")
            if (fileUri != null && fileUri.toString().isNotBlank() && fileUri.toString()
                    .isNotEmpty()
            ) {
                imageRef.putFile(fileUri).await()
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    user.avatarUrl = uri.toString()
                    fireStore.collection("users").document(user.username).set(user)
                        .addOnSuccessListener {
                            trySendBlocking(MainResult.Success(Unit))
                        }.addOnFailureListener {
                            trySendBlocking(MainResult.Message(it.message.toString()))
                        }.addOnCanceledListener {
                            trySendBlocking(MainResult.Message("Rejected"))
                        }

                }.addOnFailureListener {
                    trySendBlocking(MainResult.Message(it.message.toString()))
                }.addOnCanceledListener {
                    trySendBlocking(MainResult.Message("Rejected"))
                }

            } else {
                fireStore.collection("users").document(user.username).set(user)
                    .addOnSuccessListener {
                        trySendBlocking(MainResult.Success(Unit))
                    }.addOnFailureListener {
                        trySendBlocking(MainResult.Message(it.message.toString()))
                    }.addOnCanceledListener {
                        trySendBlocking(MainResult.Message("Rejected"))
                    }
            }
            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))
        }

    override suspend fun changeDoctorAvailability(checked: Boolean) {
        dataStore.data.collect {
            val username = it[USERNAME] ?: ""
            fireStore.collection("users").document(username).update("available", checked)
        }
    }


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