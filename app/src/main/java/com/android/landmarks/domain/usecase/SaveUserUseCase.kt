package com.android.landmarks.domain.usecase

import com.android.landmarks.domain.model.User
import com.android.landmarks.domain.repository.UserRepository
import com.android.landmarks.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

/**
 * An interactor that calls the actual implementation of [UserViewModel](which is injected by DI)
 * it handles the response that returns data &
 * contains a list of actions, event steps
 */
class SaveUserUseCase @Inject constructor(private val repository: UserRepository) :
    SingleUseCase<Boolean>() {
    private var mUser: User? = null

    fun setUser(user: User) {
        mUser = user
    }

    override fun buildUseCaseSingle(): Single<Boolean> {
        return repository.saveUser(mUser!!)
    }
}
