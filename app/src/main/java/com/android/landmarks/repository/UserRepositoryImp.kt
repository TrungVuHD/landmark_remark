package com.android.landmarks.repository

import android.content.SharedPreferences
import com.android.landmarks.domain.model.User
import com.android.landmarks.domain.repository.UserRepository
import com.android.landmarks.util.Constants
import com.android.landmarks.util.UserNotFoundException
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import io.reactivex.SingleEmitter

/**
 * This repository is responsible for
 * fetching data[User] from db and SharedPreferences
 * */
class UserRepositoryImp(
    private val sharedPreferences: SharedPreferences,
    private val firebaseFirestore: FirebaseFirestore
) : UserRepository {
    companion object {
        const val DEFAULT_USERNAME_KEY = "user_name"
    }

    override fun getUserProfile(userName: String): Single<User> {
        return Single.create { emitter: SingleEmitter<User> ->
            firebaseFirestore.collection(Constants.USERS_CHILD)
                .document(userName)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = it.result.toObject(User::class.java)
                        if (user != null) {
                            sharedPreferences.edit().putString(DEFAULT_USERNAME_KEY, user.name)
                                .apply()
                            emitter.onSuccess(user)
                        } else {
                            emitter.onError(UserNotFoundException())
                        }
                    } else {
                        emitter.onError(RuntimeException(it.exception))
                    }
                }
        }
    }

    override fun saveUser(user: User): Single<Boolean> {
        return Single.create { emitter ->
            firebaseFirestore.collection(Constants.USERS_CHILD)
                .document(user.name)
                .set(user)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        sharedPreferences.edit().putString(DEFAULT_USERNAME_KEY, user.name).apply()
                        emitter.onSuccess(true)
                    } else {
                        emitter.onError(RuntimeException(it.exception))
                    }
                }
        }
    }

    override fun getUserName(): String {
        return sharedPreferences.getString(DEFAULT_USERNAME_KEY, "").toString()
    }
}
