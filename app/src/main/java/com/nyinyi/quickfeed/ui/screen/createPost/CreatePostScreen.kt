package com.nyinyi.quickfeed.ui.screen.createPost

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GifBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.nyinyi.domain_model.UserProfile
import com.nyinyi.quickfeed.R
import com.nyinyi.quickfeed.ui.components.CircleProfileIcon
import com.nyinyi.quickfeed.ui.components.DialogState
import com.nyinyi.quickfeed.ui.components.StatusDialog
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme
import java.io.InputStream

@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel = hiltViewModel(),
    onBackPress: () -> Unit = {},
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var dialogState by remember { mutableStateOf(DialogState()) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CreatePostEvent.Error -> {
                    dialogState =
                        DialogState(
                            title = "Error",
                            messageString = event.message,
                            show = true,
                            isError = true,
                        )
                }

                is CreatePostEvent.Success -> {
                    onBackPress()
                }
            }
        }
    }

    if (dialogState.show) {
        StatusDialog(
            dialogState = dialogState,
            onDismiss = {
                dialogState = dialogState.copy(show = false)
                if (dialogState.isError) {
                    viewModel.clearError()
                }
            },
        )
    }
    if (uiState.isLoading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else if (
        uiState.error != null
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier =
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    } else {
        CreatePostContent(
            userProfile = uiState.userProfile,
            onBackPress = onBackPress,
            onPostSubmit = { postText, imageStream ->
                viewModel.createPost(
                    postContent = postText,
                    postImage = imageStream,
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostContent(
    userProfile: UserProfile?,
    onBackPress: () -> Unit,
    onPostSubmit: (String, InputStream?) -> Unit,
) {
    var postText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<InputStream?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            selectedImage = uri
            selectedImageUri = uri?.let { context.contentResolver.openInputStream(it) }
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create Post",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    FilledIconButton(
                        onClick = onBackPress,
                        colors =
                            IconButtonDefaults.filledIconButtonColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.8f,
                                    ),
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = postText.isNotBlank() || selectedImageUri != null,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut(),
                    ) {
                        Button(
                            onClick = {
                                if ((postText.isNotBlank() || selectedImageUri != null)) {
                                    onPostSubmit(postText, selectedImageUri)
                                }
                            },
                            enabled = (postText.isNotBlank() || selectedImageUri != null),
                            shape = RoundedCornerShape(24.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            contentPadding =
                                PaddingValues(
                                    horizontal = 24.dp,
                                    vertical = 12.dp,
                                ),
                            modifier =
                                Modifier
                                    .padding(end = 8.dp)
                                    .shadow(
                                        elevation = if (postText.isNotBlank() || selectedImageUri != null) 4.dp else 0.dp,
                                        shape = RoundedCornerShape(24.dp),
                                    ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    Icons.Outlined.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    "Share",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                modifier = Modifier.shadow(elevation = 1.dp),
            )
        },
        bottomBar = {
            CreatePostBottomBar(
                onImageSelectClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
                onGifClick = { /* TODO */ },
                onPollClick = { /* TODO */ },
                onEmojiClick = { /* TODO */ },
                onLocationClick = { /* TODO */ },
                hasImage = selectedImageUri != null,
            )
        },
        modifier =
            Modifier
                .fillMaxSize()
                .imePadding(),
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
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
                                imageUrl = userProfile?.profilePictureUrl,
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

            // Selected Image Preview
            selectedImage?.let { uri ->
                item {
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
                                                .data(data = uri)
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
                                    selectedImage = null
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
        }
    }
}

@Composable
fun CreatePostBottomBar(
    onImageSelectClick: () -> Unit,
    onGifClick: () -> Unit,
    onPollClick: () -> Unit,
    onEmojiClick: () -> Unit,
    onLocationClick: () -> Unit,
    hasImage: Boolean = false,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                BottomBarIconButton(
                    icon = Icons.Default.AddAPhoto,
                    label = "Photo",
                    onClick = onImageSelectClick,
                    isSelected = hasImage,
                    selectedColor = MaterialTheme.colorScheme.primary,
                )
            }
            item {
                BottomBarIconButton(
                    icon = Icons.Default.GifBox,
                    label = "GIF",
                    onClick = onGifClick,
                )
            }
            item {
                BottomBarIconButton(
                    icon = Icons.Default.Poll,
                    label = "Poll",
                    onClick = onPollClick,
                )
            }
            item {
                BottomBarIconButton(
                    icon = Icons.Default.TagFaces,
                    label = "Emoji",
                    onClick = onEmojiClick,
                )
            }
            item {
                BottomBarIconButton(
                    icon = Icons.Default.AddLocationAlt,
                    label = "Location",
                    onClick = onLocationClick,
                )
            }
        }
    }
}

@Composable
fun BottomBarIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
) {
    val backgroundColor by animateColorAsState(
        selectedColor.copy(alpha = 0.15f),
        animationSpec = tween(300),
        label = "background_color",
    )

    val contentColor by animateColorAsState(
        targetValue =
            if (isSelected) {
                selectedColor
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        animationSpec = tween(300),
        label = "content_color",
    )

    Surface(
        onClick = onClick,
        modifier =
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            AnimatedVisibility(
                visible = isSelected,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut(),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Create Post Light")
@Composable
fun CreatePostScreenPreviewLight() {
    QuickFeedTheme(darkTheme = false) {
        CreatePostScreen(onBackPress = {})
    }
}

@Preview(showBackground = true, name = "Create Post Dark")
@Composable
fun CreatePostScreenPreviewDark() {
    QuickFeedTheme(darkTheme = true) {
        CreatePostScreen(onBackPress = {})
    }
}

@Preview(showBackground = true, name = "Create Post With Image Light")
@Composable
fun CreatePostScreenWithImagePreviewLight() {
    QuickFeedTheme(darkTheme = false) {
        val state =
            remember { mutableStateOf<String?>("https://picsum.photos/seed/preview/600/400") }
        CreatePostScreen(
            onBackPress = {},
        )
    }
}
