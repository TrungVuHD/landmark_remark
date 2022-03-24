package com.android.landmarks.data.repository

import com.android.landmarks.domain.model.User
import com.android.landmarks.domain.repository.UserRepository
import io.reactivex.Single
import io.reactivex.SingleEmitter

class FakeUserRepository : UserRepository {

    var user: User? = null

    override fun getUserProfile(userName: String): Single<User> {
        return Single.create { emitter: SingleEmitter<User> ->
            user?.let {
                if (userName == it.name) {
                    emitter.onSuccess(it)
                } else {
                    emitter.onError(RuntimeException())
                }
            } ?: run {
                emitter.onError(RuntimeException())
            }

        }
    }

    override fun saveUser(user: User): Single<Boolean> {
        return Single.create { emitter: SingleEmitter<Boolean> ->
            this.user = user
            emitter.onSuccess(true)
        }
    }

    override fun getUserName(): String {
        user?.let {
            return it.name
        } ?: run {
            return ""
        }
    }
}