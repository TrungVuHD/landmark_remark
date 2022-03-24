package com.android.landmarks.presentation.notesmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.landmarks.R
import com.android.landmarks.presentation.notes.NotesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesManagerActivity : AppCompatActivity(), NotesManagerCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_manager)

        if (savedInstanceState == null) {
            navigateToNotesPage()
        }
    }

    private fun navigateToNotesPage() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.notes_manager_container,
                NotesFragment.newInstance(),
                NotesFragment.FRAGMENT_NAME
            ).commitAllowingStateLoss()
    }
}
