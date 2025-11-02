package com.jetpack.notes.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao // defines the operations you can do on that table (insert, read, delete, update)
interface NoteDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>> // UI can observe changes automatically as data Updates

//    @Query("DELETE FROM notes")
    @Delete
    suspend fun deleteNotes(note : Note)

    @Update
    suspend fun updateNotes(note: Note)
}