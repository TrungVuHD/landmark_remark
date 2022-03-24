package com.android.landmarks.domain.usecase

import com.android.landmarks.domain.model.User
import com.android.landmarks.domain.repository.UserRepository
import com.android.landmarks.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

/**
 * An interactor that calls the actual implementation of [UserViewModel](which is injected by DI)
 * it handles the response that returns data
 */
class GetUserUseCase @Inject constructor(private val repository: UserRepository) :
    SingleUseCase<User>() {
    private var mUserName: String? = null

    fun setUserName(userName: String) {
        mUserName = userName
    }

    override fun buildUseCaseSingle(): Single<User> {
        return repository.getUserProfile(mUserName!!)
    }

    fun getLocalUsername(): String {
        return repository.getUserName()
    }
}
