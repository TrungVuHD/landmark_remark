package com.android.landmarks

import android.annotation.SuppressLint
import com.android.landmarks.data.repository.FakeNotesRepository
import com.android.landmarks.domain.repository.NotesRepository
import com.android.landmarks.util.TestUtil
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class NotesRepositoryTest {

    lateinit var notesRepository: NotesRepository

    @Before
    fun setup() {
        notesRepository = FakeNotesRepository()
    }

    @After
    fun tearDown() {
    }

    @SuppressLint("CheckResult")
    @Test
    fun test_get_notes() {
        val fakeName = UUID.randomUUID().toString()
        for (note in TestUtil.makeNotesList(10, fakeName)) {
            notesRepository.saveNote(note)
        }

        notesRepository.getNotes()
            .subscribe(
                { v -> Assert.assertEquals(v.size.toLong(), 10) },
                { e -> Assert.assertTrue(false) }
            )
    }

    @SuppressLint("CheckResult")
    @Test
    fun test_get_my_notes() {
        val fakeName = UUID.randomUUID().toString()
        for (note in TestUtil.makeNotesList(10, fakeName)) {
            notesRepository.saveNote(note)
        }
        val fakeName1 = UUID.randomUUID().toString()
        for (note in TestUtil.makeNotesList(8, fakeName1)) {
            notesRepository.saveNote(note)
        }

        notesRepository.getMyNotes(fakeName)
            .subscribe(
                { v -> Assert.assertEquals(v.size.toLong(), 10) },
                { e -> Assert.assertTrue(false) }
            )

        notesRepository.getMyNotes(fakeName1)
            .subscribe(
                { v -> Assert.assertEquals(v.size.toLong(), 8) },
                { e -> Assert.assertTrue(false) }
            )
    }


    @SuppressLint("CheckResult")
    @Test
    fun test_search() {
        val fakeName = UUID.randomUUID().toString()
        for (note in TestUtil.makeNotesList(10, fakeName)) {
            notesRepository.saveNote(note)
        }

        notesRepository.searchByUserNameOrNoteContent(fakeName)
            .subscribe(
                { v ->
                    Assert.assertTrue(v.isNotEmpty())
                },
                { e -> Assert.assertTrue(false) }
            )

        notesRepository.searchByUserNameOrNoteContent(
            "skdbgnsjkdgsa ashbd;fihasp; asdnf asd",
        )
            .subscribe(
                { v -> Assert.assertEquals(v.size.toLong(), 0) },
                { e -> Assert.assertTrue(false) }
            )
    }
}