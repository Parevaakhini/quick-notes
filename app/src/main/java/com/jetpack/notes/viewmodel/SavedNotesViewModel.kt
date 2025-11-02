package com.jetpack.notes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.notes.model.AppDatabaseSingleton
import com.jetpack.notes.model.Note
import com.jetpack.notes.model.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedNotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : NotesRepository
    private  val _notesList = MutableStateFlow<List<Note>>(emptyList())
    val notesList : StateFlow<List<Note>> = _notesList

    init {
        val dao = AppDatabaseSingleton.getDatabase(application).noteDao()
        repository = NotesRepository(dao)
        getAllNotes()
    }

    private fun getAllNotes(){
        viewModelScope.launch {
            repository.getAllNotes().collectLatest { notes ->
                _notesList.value = notes
            }
        }
    }

    fun deleteNote(note : Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteNotes(note)
        }
    }
}