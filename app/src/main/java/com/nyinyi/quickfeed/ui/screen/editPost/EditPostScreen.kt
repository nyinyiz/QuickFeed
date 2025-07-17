package com.nyinyi.quickfeed.ui.screen.editPost

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.nyinyi.domain_model.Post
import com.nyinyi.quickfeed.R
import com.nyinyi.quickfeed.ui.components.CircleProfileIcon
import timber.log.Timber
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    viewModel: EditPostViewModel = hiltViewModel(),
    onBack: () -> Unit,
    postId: String
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(postId.isNotBlank()) {
        viewModel.getPostDetail(postId)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is EditPostErrorViewEvent.ErrorOnUpdate -> {
                    // SHOW ERROR on Update
                }

                is EditPostErrorViewEvent.ErrorOnLoading -> {
                    // SHOW ERROR
                }

                is EditPostErrorViewEvent.Success -> {
                    Timber.d("Success updated")
                    onBack()
                }
            }
        }

    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Edit Post", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.value.isLoading) {
                EditPostScreenLoading()
                // show loading
            } else if (state.value.error?.isNotBlank() == true) {
                // show error
                EditPostScreenError(state.value.error ?: "Unknown error")
            } else {
                state.value.post?.let {
                    EditPostScreenContent(
                        post = it,
                        onUpdateClick = { inputStream, content ->
                            Timber.d("Updated by nyi: $inputStream, $content")
                            viewModel.updatePost(
                                image = inputStream,
                                content = content
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditPostScreenContent(
    post: Post,
    onUpdateClick: (InputStream?, String) -> Unit
) {
    var postText by remember { mutableStateOf(post.content) }
    var editPostImage by remember { mutableStateOf(post.imageUrl) }
    var selectedImageInputStream by remember { mutableStateOf<InputStream?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            selectedImageUri = uri
            selectedImageInputStream = uri?.let {
                context.contentResolver.openInputStream(uri)
            }
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item(
            key = "post_content",
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceContainer.copy(
                                alpha = 0.5f,
                            ),
                    ),
                shape = RoundedCornerShape(20.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box {
                        CircleProfileIcon(
                            imageUrl = post?.authorProfilePictureUrl,
                            size = 56.dp,
                            shadowElevation = 4.dp,
                        )
                        Surface(
                            modifier =
                                Modifier
                                    .size(16.dp)
                                    .align(Alignment.BottomEnd),
                            shape = CircleShape,
                            color = Color(0xFF4CAF50),
                            shadowElevation = 2.dp,
                        ) {}
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = postText,
                            onValueChange = {
                                postText = it
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 120.dp),
                            placeholder = {
                                Text(
                                    "What's on your mind today?",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color =
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.6f,
                                        ),
                                )
                            },
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedBorderColor =
                                        MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.5f,
                                        ),
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = MaterialTheme.colorScheme.primary,
                                ),
                            textStyle =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                ),
                            shape = RoundedCornerShape(16.dp),
                            maxLines = 8,
                        )
                    }
                }
            }
        }

        if (editPostImage?.isNotBlank() == true) {
            item(
                key = "post_image",
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceContainer.copy(
                                    alpha = 0.3f,
                                ),
                        ),
                ) {
                    Box(
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 4.dp,
                        ) {
                            Image(
                                painter =
                                    rememberAsyncImagePainter(
                                        ImageRequest
                                            .Builder(LocalContext.current)
                                            .data(data = selectedImageUri ?: editPostImage)
                                            .apply(block = fun ImageRequest.Builder.() {
                                                crossfade(true)
                                                placeholder(R.drawable.ic_launcher_foreground)
                                                error(R.drawable.ic_launcher_foreground)
                                            })
                                            .build(),
                                    ),
                                contentDescription = "Selected Image",
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                            )
                        }

                        FilledIconButton(
                            onClick = {
                                selectedImageUri = null
                                selectedImageInputStream = null
                            },
                            modifier =
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .size(36.dp),
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                ),
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove Image",
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        // Image info overlay
                        Surface(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
                        ) {
                            Row(
                                modifier =
                                    Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp,
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    "Image attached",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }
        }

        item(
            key = "edit_post_button",
        ) {
            ElevatedButton(
                onClick = {
                    onUpdateClick(
                        selectedImageInputStream,
                        postText
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Edit Post",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                )
            }
        }

    }

}

@Composable
fun EditPostScreenError(error: String) {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize(),
            text = error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EditPostScreenLoading() {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}