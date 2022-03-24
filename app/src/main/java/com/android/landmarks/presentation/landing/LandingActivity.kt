package com.android.landmarks.presentation.landing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.landmarks.R
import com.android.landmarks.databinding.ActivityLandingBinding
import com.android.landmarks.presentation.notesmanager.NotesManagerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {
    private lateinit var activityLandingBinding: ActivityLandingBinding
    private val viewModel: LandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        activityLandingBinding = DataBindingUtil.setContentView(this, R.layout.activity_landing)
        activityLandingBinding.apply {
            landingViewModel = viewModel
            landingBtSave.setOnClickListener {
                val username = landingEtUser.text.toString()
                if (viewModel.isValid(username)) {
                    viewModel.setUserName(username)
                } else {
                    Toast.makeText(
                        this@LandingActivity,
                        R.string.landing_valid_username_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            viewModel.isLoading.observe(
                this@LandingActivity
            ) {
                it?.let { visibility ->
                    landingProgressBar.visibility =
                        if (visibility) View.VISIBLE else View.GONE
                }
            }
            landingEtUser.setText(viewModel.getCurrentUserName())
        }

        viewModel.currentUserLiveData.observe(
            this@LandingActivity
        ) {
            val landmarkManagerIntent =
                Intent(this@LandingActivity, NotesManagerActivity::class.java)
            startActivity(landmarkManagerIntent)
            finish()
        }
    }
}
