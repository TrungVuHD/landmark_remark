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
class GetNotesUseCase @Inject constructor(private val repository: NotesRepository) :
    SingleUseCase<List<Note>>() {


    override fun buildUseCaseSingle(): Single<List<Note>> {
        return repository.getNotes()
    }
}
