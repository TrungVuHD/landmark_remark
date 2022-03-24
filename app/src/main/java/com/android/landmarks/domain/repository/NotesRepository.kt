package com.android.landmarks.domain.repository

import com.android.landmarks.domain.model.Note
import io.reactivex.Single

/**
 * To make an interaction between
 * [NotesRepositoryImp] & [GetNotesUseCase]
 * & [SearchNotesUseCase] & [SaveNoteUseCase]
 * */
interface NotesRepository {
    fun getNotes(): Single<List<Note>>
    fun getMyNotes(userName: String): Single<List<Note>>
    fun searchByUserNameOrNoteContent(searchQuery: String): Single<List<Note>>
    fun saveNote(note: Note): Single<Boolean>
}