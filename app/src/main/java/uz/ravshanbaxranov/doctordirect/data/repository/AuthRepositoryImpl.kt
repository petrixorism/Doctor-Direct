package uz.ravshanbaxranov.doctordirect.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.domain.repository.AuthRepository
import uz.ravshanbaxranov.doctordirect.other.Constants
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val fireStore: DocumentReference,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {

    override suspend fun createUserData(userData: User, fileUri: Uri): Flow<MainResult<Unit>> =
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

    override suspend fun loginUser(username: String, password: String): Flow<MainResult<String>> =
        callbackFlow<MainResult<String>> {

            trySendBlocking(MainResult.Loading(true))

            val user = fireStore.collection("users").document(username).get().await()
                .toObject(User::class.java)

            if (user != null && user.password == password) {
                launch(Dispatchers.IO) {
                    dataStore.edit { preferences ->
                        preferences[Constants.IS_LOGGED_IN] = true
                        preferences[Constants.USERNAME] = username
                    }
                }
                trySendBlocking(MainResult.Success(user.role))
            } else {
                trySendBlocking(MainResult.Message("User not found"))
            }

            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))

        }

    override suspend fun getUserData(userName: String): Flow<MainResult<User?>> =
        callbackFlow<MainResult<User?>> {

            trySendBlocking(MainResult.Loading(true))

            val user = fireStore.collection("users").document(userName).get().await()
                .toObject(User::class.java)

            trySendBlocking(MainResult.Success(user))

            trySendBlocking(MainResult.Loading(false))

            awaitClose {}
        }.flowOn(Dispatchers.IO).catch {
            emit(MainResult.Loading(false))
            emit(MainResult.Message(it.message.toString()))

        }

    override suspend fun signOut() {
        dataStore.edit {
            it[Constants.IS_LOGGED_IN] = false
            it[Constants.USERNAME] = ""
        }
    }
}