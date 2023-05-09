package uz.ravshanbaxranov.doctordirect.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import uz.ravshanbaxranov.doctordirect.data.model.MainResult
import uz.ravshanbaxranov.doctordirect.data.model.remote.User

interface AuthRepository {

    suspend fun createUserData(userData: User, fileUri: Uri): Flow<MainResult<Unit>>

    suspend fun loginUser(username: String, password: String): Flow<MainResult<String>>

    suspend fun getUserData(userName: String): Flow<MainResult<User?>>

    suspend fun signOut()

}