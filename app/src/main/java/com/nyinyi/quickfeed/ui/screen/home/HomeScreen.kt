package com.nyinyi.quickfeed.ui.screen.home

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyinyi.quickfeed.ui.components.DialogState
import com.nyinyi.quickfeed.ui.components.StatusDialog

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    logOutSuccess: () -> Unit,
    onClickCreatePost: () -> Unit = {},
    onClickSetting: () -> Unit = {},
    onClickProfile: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var dialogState by remember { mutableStateOf(DialogState()) }

    LaunchedEffect(Unit) {
        viewModel.loadTimelinePosts()
    }

    LaunchedEffect(Unit) {
        viewModel.checkProfileCompletion()
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.Error -> {
                    dialogState =
                        DialogState(
                            title = "Error",
                            messageString = event.errorMessage,
                            show = true,
                            isError = true,
                        )
                }

                is HomeEvent.LogOutSuccess -> {
                    logOutSuccess()
                }

                is HomeEvent.PostCreationSuccess -> {
                }
            }
        }
    }

    if (uiState.userProfileNotCompleted) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text("Complete Your Profile") },
            text = { Text("Please complete your profile to continue enjoying the timeline experience.") },
            confirmButton = {
                TextButton(onClick = onClickProfile) {
                    Text("Go to Profile")
                }
            },
            dismissButton = null,
        )
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

    TwitterTimelineScreen(
        uiState = uiState,
        onClickCreatePost = {
            onClickCreatePost()
        },
        onClickSettings = onClickSetting,
        onClickProfile = onClickProfile,
        onRefreshTimeline = {
            viewModel.loadTimelinePosts()
        },
        onClickLike = { postId ->
            viewModel.likePost(postId)
        },
        onClickUnLike = { postId ->
            viewModel.unLikePost(postId)
        },
    )
}
