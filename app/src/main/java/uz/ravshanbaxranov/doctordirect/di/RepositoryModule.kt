package uz.ravshanbaxranov.doctordirect.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.ravshanbaxranov.doctordirect.data.repository.AdminRepositoryImpl
import uz.ravshanbaxranov.doctordirect.data.repository.AuthRepositoryImpl
import uz.ravshanbaxranov.doctordirect.data.repository.UserRepositoryImpl
import uz.ravshanbaxranov.doctordirect.domain.repository.AdminRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.AuthRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun provideAdminRepository(impl: AdminRepositoryImpl): AdminRepository


}