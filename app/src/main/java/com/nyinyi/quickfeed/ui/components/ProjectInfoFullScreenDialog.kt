package com.nyinyi.quickfeed.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme

private data class ProjectInfoSection(
    val title: String,
    val content: String,
    val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectInfoFullScreenDialog(onDismissRequest: () -> Unit) {
    val projectSections =
        listOf(
            ProjectInfoSection(
                title = "Features Included",
                icon = Icons.Filled.CheckCircle,
                content =
                    """
                    - User Authentication (Login, Signup, Logout)
                    - Real-time Updates (Firestore listeners)
                    - View Timeline/Feed of Posts
                    - Create New Posts (Text only / Image only / Text + Image)
                    - Delete Own Posts
                    - Like/Unlike Posts
                    - View My Profile
                    - Profile Editing (Display Name, Handle, Bio, Profile Picture)
                    - Dark/Light Mode Support
                    - Settings Screen (Theme Toggle, Logout, Developer Info)
                    """.trimIndent(),
            ),
            ProjectInfoSection(
                title = "Features Not Completed / Future Ideas",
                icon = Icons.Filled.HighlightOff,
                content =
                    """
                    - Edit Own Posts
                    - Commenting on Posts
                    - User Search
                    - Following/Followers System
                    - Notifications
                    - Offline Support/Caching
                    """.trimIndent(),
            ),
            ProjectInfoSection(
                title = "Technology Stack & Libraries",
                icon = Icons.Filled.BuildCircle,
                content =
                    """
                    - UI: Jetpack Compose (Kotlin)
                    - Architecture: MVVM
                    - Dependency Injection: Hilt
                    - Asynchronous Programming: Kotlin Coroutines & Flow
                    - Navigation: Jetpack Navigation Compose
                    - Image Loading: Coil
                    - Backend: Firebase (Authentication, Firestore)
                    - Supabase (Storage)
                    - Data Layer: Repository Pattern
                    - Multi Module Architecture
                    """.trimIndent(),
            ),
            ProjectInfoSection(
                title = "Project Notes",
                icon = Icons.Filled.Info,
                content =
                    """
                    This project was developed as an assignment to demonstrate modern Android development practices,
                    focusing on clean architecture and Jetpack libraries.
                    """.trimIndent(),
            ),
        )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true,
                dismissOnBackPress = true,
            ),
    ) {
        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                        ),
                    ).background(MaterialTheme.colorScheme.surface),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "About This Project",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.surface),
                contentPadding =
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp,
                    ),
            ) {
                items(projectSections) { section ->
                    ProjectInfoSectionCard(
                        icon = section.icon,
                        title = section.title,
                        content = section.content,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Spacer(
                        modifier = Modifier.height(150.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectInfoSectionCard(
    icon: ImageVector,
    title: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape =
            androidx.compose.foundation.shape
                .RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                        1.dp,
                    ),
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProjectInfoDialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun ProjectInfoDialogContent(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 20.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 8.dp),
    )
}

@Preview(showBackground = false)
@Composable
fun ProjectInfoFullScreenDialogPreview() {
    QuickFeedTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.5f)),
        ) {
            ProjectInfoFullScreenDialog(onDismissRequest = {})
        }
    }
}
