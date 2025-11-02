package com.jetpack.notes.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jetpack.notes.ui.theme.PurpleGrey40
import com.jetpack.notes.ui.theme.skyblue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderTheme(
    title: String,
    navController: NavController? = null,
    showBackButton: Boolean = false,
    onBellClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PurpleGrey40
            )
        },
        navigationIcon = {
            if (showBackButton) {
                    IconButton(onClick = {
                        navController?.navigate("home_screen") {
                            popUpTo("home_screen") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = PurpleGrey40
                        )
                    }

            } else {
                null
            }
        },

        actions = {
            if(onBellClick != null) {
                IconButton(onClick = { onBellClick.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = PurpleGrey40
                    )
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = skyblue
        )
    )
}
