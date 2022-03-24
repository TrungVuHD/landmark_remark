package com.android.landmarks.repository

import com.android.landmarks.domain.model.Note
import com.android.landmarks.domain.repository.NotesRepository
import com.android.landmarks.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import io.reactivex.SingleEmitter


/**
 * This repository is responsible for
 * fetching data[Note] from db
 * */
class NotesRepositoryImp(private val firebaseFirestore: FirebaseFirestore) :
    NotesRepository {

    override fun getNotes(): Single<List<Note>> {
        return Single.create { emitter: SingleEmitter<List<Note>> ->
            firebaseFirestore.collection(Constants.NOTES_CHILD)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val notesList = ArrayList<Note>()
                        for (document in it.result) {
                            notesList.add(document.toObject(Note::class.java))
                        }
                        emitter.onSuccess(notesList)
                    } else emitter.onError(RuntimeException(it.exception))
                }
        }

    }

    override fun getMyNotes(userName: String): Single<List<Note>> {
        return Single.create { emitter: SingleEmitter<List<Note>> ->
            firebaseFirestore.collection(Constants.NOTES_CHILD)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val notesList = ArrayList<Note>()
                        for (document in it.result) {
                            val note = document.toObject(Note::class.java)
                            if (note.userName.equals(userName, false)) {
                                notesList.add(note)
                            }
                        }
                        emitter.onSuccess(notesList)
                    } else emitter.onError(RuntimeException(it.exception))
                }
        }
    }

    override fun searchByUserNameOrNoteContent(searchQuery: String): Single<List<Note>> {
        return Single.create { emitter: SingleEmitter<List<Note>> ->
            firebaseFirestore.collection(Constants.NOTES_CHILD)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val notesList = ArrayList<Note>()
                        for (document in it.result) {
                            val note = document.toObject(Note::class.java)
                            if (note.userName.equals(searchQuery, true) || note.remark.contains(
                                    searchQuery,
                                    true
                                )
                            ) {
                                notesList.add(note)
                            }

                        }
                        emitter.onSuccess(notesList)
                    } else emitter.onError(RuntimeException(it.exception))
                }
        }
    }

    override fun saveNote(note: Note): Single<Boolean> {
        return Single.create { emitter ->
            firebaseFirestore.collection(Constants.NOTES_CHILD)
                .document(note.remark)
                .set(note)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(true)
                    } else {
                        emitter.onError(RuntimeException(it.exception))
                    }
                }
        }
    }
}
