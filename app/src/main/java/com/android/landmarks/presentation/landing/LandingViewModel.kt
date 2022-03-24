package com.android.landmarks.presentation.landing

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.landmarks.domain.model.User
import com.android.landmarks.domain.usecase.GetUserUseCase
import com.android.landmarks.domain.usecase.SaveUserUseCase
import com.android.landmarks.util.UserNotFoundException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val currentUserLiveData = MutableLiveData<User>()

    init {
        isLoading.value = false
    }

    /**
     * Set the user name
     * @param userName
     */
    fun setUserName(userName: String) {
        isLoading.value = true
        if (!TextUtils.isEmpty(userName)) {
            val trimmedUserName = userName.trim { it <= ' ' }
            getUserUseCase.setUserName(trimmedUserName)
            getUserUseCase.execute(
                onSuccess = {
                    isLoading.value = false
                    currentUserLiveData.value = it
                },
                onError = {
                    isLoading.value = false
                    if (it is UserNotFoundException) {
                        val user = User()
                        user.id = UUID.randomUUID().toString()
                        user.name = trimmedUserName
                        saveNewUser(user)
                    } else {
                        it.printStackTrace()
                    }
                }
            )
        }
    }

    /**
     * Set the user data
     * @param user
     */
    private fun saveNewUser(user: User) {
        isLoading.value = true
        saveUserUseCase.setUser(user)
        saveUserUseCase.execute(
            onSuccess = {
                isLoading.value = false
                currentUserLiveData.value = user
            },
            onError = {
                isLoading.value = false
                it.printStackTrace()
            })
    }

    /**
     * Checks if the input is valid or not
     * @param userName
     * @return true if it's valid
     */
    fun isValid(userName: String): Boolean {
        return userName.trim { it <= ' ' }.isNotEmpty()
    }

    /**
     * @return name of current user profile
     */
    fun getCurrentUserName(): String {
        return getUserUseCase.getLocalUsername()
    }
}
