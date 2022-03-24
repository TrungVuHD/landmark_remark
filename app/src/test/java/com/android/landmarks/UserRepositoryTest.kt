package com.android.landmarks

import android.annotation.SuppressLint
import com.android.landmarks.data.repository.FakeUserRepository
import com.android.landmarks.domain.model.User
import com.android.landmarks.domain.repository.UserRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class UserRepositoryTest {

    lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userRepository = FakeUserRepository()
    }

    @After
    fun tearDown() {
    }

    @SuppressLint("CheckResult")
    @Test
    fun test_save_user() {
        val user = User()
        user.id = UUID.randomUUID().toString()
        user.name = "Test123"
        userRepository.saveUser(user)
            .subscribe(
                { v -> Assert.assertEquals("Test123", userRepository.getUserName()) },
                { e -> Assert.assertTrue(false) }
            )
    }

    @SuppressLint("CheckResult")
    @Test
    fun test_get_user_profile() {
        val user = User()
        user.id = UUID.randomUUID().toString()
        user.name = "Test456"
        userRepository.saveUser(user)
            .subscribe(
                { v ->
                    userRepository.getUserProfile("Test456").subscribe({ v1 ->
                        Assert.assertNotNull(v1)
                    }, { e1 ->
                        Assert.assertTrue(false)
                    })

                },
                { e -> Assert.assertTrue(false) }
            )
    }
}