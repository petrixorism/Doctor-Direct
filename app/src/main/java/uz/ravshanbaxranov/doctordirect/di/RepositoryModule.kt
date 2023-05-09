package uz.ravshanbaxranov.doctordirect.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.ravshanbaxranov.doctordirect.data.repository.AdminRepositoryImpl
import uz.ravshanbaxranov.doctordirect.data.repository.AuthRepositoryImpl
import uz.ravshanbaxranov.doctordirect.data.repository.DoctorRepositoryImpl
import uz.ravshanbaxranov.doctordirect.data.repository.GeneralRepositoryImpl
import uz.ravshanbaxranov.doctordirect.data.repository.UserRepositoryImpl
import uz.ravshanbaxranov.doctordirect.domain.repository.AdminRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.AuthRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.DoctorRepository
import uz.ravshanbaxranov.doctordirect.domain.repository.GeneralRepository
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

    @Binds
    @Singleton
    fun provideDoctorRepository(impl: DoctorRepositoryImpl): DoctorRepository

    @Binds
    @Singleton
    fun provideGeneralRepository(impl: GeneralRepositoryImpl): GeneralRepository


}