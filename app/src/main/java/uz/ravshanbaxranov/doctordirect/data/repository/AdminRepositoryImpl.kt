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
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val fireStore: DocumentReference,
    private val dataStore: DataStore<Preferences>
) : AdminRepository {

    override suspend fun createDoctor(userData: User, fileUri: Uri): Flow<MainResult<Unit>> =
        callbackFlow<MainResult<Unit>> {

            trySendBlocking(MainResult.Loading(true))

            val cloudStorage = Firebase.storage.reference

            val idImage = fireStore.collection("id").document().id
            val userId = fireStore.collection("id").document().id


            val imageRef = cloudStorage.child("images/$idImage")
            imageRef.putFile(fileUri).await()
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                userData.uid = userId
                userData.avatarUrl = uri.toString()

                val bookRef = fireStore.collection("users")
                bookRef.document(userData.username).set(userData).addOnSuccessListener {
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

            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))
        }


    override suspend fun deleteDoctor(username: String): Flow<MainResult<Unit>> =
        callbackFlow<MainResult<Unit>> {
            trySendBlocking(MainResult.Loading(true))

            fireStore.collection("users").document(username).delete().await()
            trySendBlocking(MainResult.Success(Unit))
            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))
        }

    override suspend fun getAllDoctors(): Flow<MainResult<List<User>>> =
        callbackFlow<MainResult<List<User>>> {
            trySendBlocking(MainResult.Loading(true))


            fireStore.collection("users").addSnapshotListener { value, e ->
                val users = ArrayList<User>()
                for (i in value!!.documents) {
                    val userData = i.toObject(User::class.java)!!
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
}