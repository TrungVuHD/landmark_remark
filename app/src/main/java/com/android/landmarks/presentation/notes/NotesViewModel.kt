package com.android.landmarks.presentation.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.landmarks.domain.model.Coordinates
import com.android.landmarks.domain.model.Note
import com.android.landmarks.domain.usecase.*
import com.android.landmarks.util.LocationLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

/**To store & manage UI-related data in a lifecycle conscious way!
 * this class allows data to survive configuration changes such as screen rotation
 * by interacting with [GetNotesUseCase]
 *
 * */
@HiltViewModel
class NotesViewModel @Inject constructor(
    application: Application,
    private val getNotesUseCase: GetNotesUseCase,
    private val createNotesUseCase: CreateNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val myNotesUseCase: MyNotesUseCase,
    private val getUserUseCase: GetUserUseCase
) : AndroidViewModel(application) {

    val notesReceivedLiveData = MutableLiveData<List<Note>>()
    val isLoadingLiveData = MutableLiveData<Boolean>()
    val isSearchingLiveData = MutableLiveData<Boolean>()
    val currentLocationLiveData = MutableLiveData<Coordinates>()
    private val searchQuery = MutableLiveData<String>()

    val locationLiveData = LocationLiveData(application)

    init {
        isLoadingLiveData.value = false
        isSearchingLiveData.value = false
        searchQuery.value = ""
    }

    val currentLocation: Coordinates? get() = currentLocationLiveData.value

    fun set(currentLocation: Coordinates) = run { currentLocationLiveData.value = currentLocation }

    val isSearching: Boolean get() = isSearchingLiveData.value == true

    /**
     * @return all the notes data
     */
    fun loadNotes() {
        isLoadingLiveData.value = true
        getNotesUseCase.execute(
            onSuccess = {
                isLoadingLiveData.value = false
                notesReceivedLiveData.value = it
            },
            onError = {
                isLoadingLiveData.value = false
                it.printStackTrace()
            }
        )
    }

    /**
     * search for notes based on input string
     * @param queryString
     */
    fun searchNotes(queryString: String) {
        if (queryString != searchQuery.value) {
            searchQuery.value = queryString
            searchNotesUseCase.setSearchQuery(queryString)
            isSearchingLiveData.value = true
            searchNotesUseCase.execute(
                onSuccess = {
                    isSearchingLiveData.value = false
                    notesReceivedLiveData.value = it
                },
                onError = {
                    isSearchingLiveData.value = false
                    it.printStackTrace()
                }
            )
        }
    }

    /**
     * get all the note created by current user
     */
    fun getMyNotes() {
        isLoadingLiveData.value = true
        myNotesUseCase.execute(
            onSuccess = {
                isLoadingLiveData.value = false
                notesReceivedLiveData.value = it
            },
            onError = {
                isLoadingLiveData.value = false
                it.printStackTrace()
            }
        )
    }

    /**
     * Adds a new note
     * @param note
     */
    fun addNote(note: String) {
        val landmark = Note()
        landmark.location = currentLocation!!
        landmark.userName = getUserUseCase.getLocalUsername()
        landmark.id = UUID.randomUUID().toString()
        landmark.remark = note

        createNotesUseCase.setLandmark(landmark)
        createNotesUseCase.execute(
            onSuccess = {
                notesReceivedLiveData.value =
                    notesReceivedLiveData.value?.plus(landmark) ?: listOf(landmark)
            },
            onError = {
                it.printStackTrace()
            }
        )
    }


    /**
     * Checks if the input is valid or not
     * @param note
     * @return true if it's valid
     */
    fun isValidNote(note: String): Boolean {
        return note.trim { it <= ' ' }.isNotEmpty()
    }

    /**
     * Checks if a location is valid or not
     * @return true if valid
     */
    fun isValidCurrentLocation(): Boolean {
        return currentLocation != null
    }

}
