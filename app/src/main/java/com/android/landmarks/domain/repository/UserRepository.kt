package com.android.landmarks.domain.repository

import com.android.landmarks.domain.model.User
import io.reactivex.Single

/**
 * To make an interaction between [UserRepositoryImp] & [GetLandmarksUseCase] & [SaveUserUseCase]
 * */
interface UserRepository {
    fun getUserProfile(userName: String): Single<User>
    fun saveUser(user: User): Single<Boolean>
    fun getUserName(): String
}