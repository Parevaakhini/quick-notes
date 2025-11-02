package com.jetpack.notes.model

import kotlinx.coroutines.flow.Flow

class NotesRepository(private val dao: NoteDao) {

    suspend fun insert(note: Note) = dao.insert(note)

    fun getAllNotes(): Flow<List<Note>> = dao.getAllNotes()

    suspend fun deleteNotes(note : Note) = dao.deleteNotes(note)

    suspend fun updateNotes(note: Note) = dao.updateNotes(note)
}
