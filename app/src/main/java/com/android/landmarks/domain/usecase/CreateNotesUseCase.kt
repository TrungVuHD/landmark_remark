package com.android.landmarks.domain.usecase

import com.android.landmarks.domain.model.Note
import com.android.landmarks.domain.repository.NotesRepository
import com.android.landmarks.domain.usecase.base.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

/**
 * An interactor that calls the actual implementation of [NotesViewModel](which is injected by DI)
 * it handles the response that returns data
 */
class CreateNotesUseCase @Inject constructor(private val repository: NotesRepository) :
    SingleUseCase<Boolean>() {
    private var note: Note? = null

    fun setLandmark(note: Note) {
        this.note = note
    }

    override fun buildUseCaseSingle(): Single<Boolean> {
        return repository.saveNote(note!!)
    }
}
