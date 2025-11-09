package com.jetpack.notes.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpack.notes.model.Note
import com.jetpack.notes.ui.theme.Pink40
import com.jetpack.notes.ui.theme.litesky
import com.jetpack.notes.ui.theme.sky1
import com.jetpack.notes.ui.theme.yellow

@Composable
fun NoteItem(note: Note, onDeleteClick: () -> Unit, onNoteClick: (Note) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable{onNoteClick(note)},
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, color = sky1),
        colors = CardDefaults.cardColors(containerColor = litesky)

    ) {
        Box(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
            )
            {
                Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = note.date, fontSize = 14.sp)
                Text(
                    text = note.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if(note.reminderTime != null) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Reminder Set",
                    tint = yellow,
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(end = 40.dp)
                )
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Note",
                    tint = Pink40
                )
            }
        }
    }
}