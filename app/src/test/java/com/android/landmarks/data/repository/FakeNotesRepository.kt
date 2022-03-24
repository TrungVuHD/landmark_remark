package com.android.landmarks.data.repository

import com.android.landmarks.domain.model.Note
import com.android.landmarks.domain.repository.NotesRepository
import io.reactivex.Single
import io.reactivex.SingleEmitter

class FakeNotesRepository : NotesRepository {
    private var notes: MutableList<Note> = ArrayList()

    override fun getNotes(): Single<List<Note>> {
        return Single.create { emitter: SingleEmitter<List<Note>> ->
            emitter.onSuccess(notes)
        }
    }

    override fun getMyNotes(userName: String): Single<List<Note>> {
        return Single.create { emitter: SingleEmitter<List<Note>> ->
            val result: MutableList<Note> = ArrayList()
            for (note in notes) {
                if (note.userName == userName) {
                    result.add(note)
                }
            }
            emitter.onSuccess(result)
        }
    }

    override fun searchByUserNameOrNoteContent(searchQuery: String): Single<List<Note>> {
        return Single.create { emitter: SingleEmitter<List<Note>> ->
            val result: MutableList<Note> = ArrayList()
            for (note in notes) {
                if (note.userName == searchQuery || note.remark.contains(searchQuery, true)) {
                    result.add(note)
                }
            }
            emitter.onSuccess(result)
        }
    }

    override fun saveNote(note: Note): Single<Boolean> {
        return Single.create { emitter: SingleEmitter<Boolean> ->
            notes.add(note)
            emitter.onSuccess(true)
        }
    }
}