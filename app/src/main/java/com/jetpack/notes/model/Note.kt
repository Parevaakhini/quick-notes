package com.jetpack.notes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "notes") // Entity create table in the name of notes in room database
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // automatically give each new row a unique ID
    val title: String,
    val date: String,
    val description: String,
    val reminderTime: Long? = null
) : Parcelable