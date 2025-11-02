package com.jetpack.notes.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.notes.model.AppDatabaseSingleton
import com.jetpack.notes.model.Note
import com.jetpack.notes.model.NotesRepository
import com.jetpack.notes.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddNotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotesRepository

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate

    private val _descriptionNote = MutableStateFlow("")
    val description: StateFlow<String> = _descriptionNote

    private val _reminderTime = MutableStateFlow<Long?>(null)
    val reminderTime: StateFlow<Long?> = _reminderTime

    private var existingNoteId: Long? = null

    init {
        val dao = AppDatabaseSingleton.getDatabase(application).noteDao()
        repository = NotesRepository(dao)
    }
    fun initialize(existingNote: Note?){
        existingNote?.let {
            _title.value = it.title
            _selectedDate.value = it.date
            _descriptionNote.value = it.description
            existingNoteId = it.id
        }
    }

    fun onTitleChange(newText: String) {
        _title.value = newText
    }

    fun onDateSelected(date: String) {
        _selectedDate.value = date
    }

    fun onDescription(note: String) {
        _descriptionNote.value = note
    }

    fun reminderTimeSelected(timeMillis: Long){
        _reminderTime.value = timeMillis
    }

    fun saveNote(context: Context,onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        when {
            title.value.isBlank() -> onComplete(false, "Please enter the title")
            selectedDate.value.isBlank() -> onComplete(false, "Please select a date")
            description.value.isBlank() -> onComplete(false, "Please enter the description")
            else -> {
                val note = Note(
                    id = existingNoteId ?: 0L,
                    title = title.value,
                    date = selectedDate.value,
                    description = description.value,
                    reminderTime = reminderTime.value
                )

                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        if(existingNoteId != null){
                            repository.updateNotes(note)
                            onComplete(true, "Note Updated successfully")
                        } else {
                            repository.insert(note)
                            onComplete(true, "Note saved successfully")
                        }

                        reminderTime.value?.let { timeMillis ->
                            val helper = NotificationHelper()
                            helper.scheduleReminder(
                                context = context,
                                timeInMillis = timeMillis,
                                title = note.title,
                                message = "Reminder for your note: ${note.title}"
                            )

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onComplete(false, "Failed to save note...")
                    }
                }
            }
        }
    }
}