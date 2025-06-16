package com.nyinyi.quickfeed.ui.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyinyi.quickfeed.ui.components.DialogState
import com.nyinyi.quickfeed.ui.components.ReusableConfirmationDialog
import com.nyinyi.quickfeed.ui.components.SettingItemWithSwitch
import com.nyinyi.quickfeed.ui.components.StatusDialog
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onThemeChange: () -> Unit = {},
    logOutSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var dialogState by remember { mutableStateOf(DialogState()) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        viewModel.getDarkModeStatus()
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SettingUiEvent.LogOutSuccess -> {
                    logOutSuccess()
                }

                is SettingUiEvent.Error -> {
                    dialogState =
                        DialogState(
                            title = "Error",
                            messageString = event.message,
                            show = true,
                            isError = true,
                        )
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
                } else {
                    viewModel.logOut()
                }
            },
        )
    }

    if (showLogoutConfirmDialog) {
        ReusableConfirmationDialog(
            showDialog = showLogoutConfirmDialog,
            onDismissRequest = {
                showLogoutConfirmDialog = false
            },
            onConfirm = {
                showLogoutConfirmDialog = false
                viewModel.logOut()
            },
            title = "Logout",
            text = "Are you sure you want to logout?",
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.4f,
                            ),
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier =
                            Modifier
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.5f,
                                    ),
                                ),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 8.dp),
        ) {
            item {
                SettingsSectionTitle("Appearance")
                SettingItemWithSwitch(
                    icon = Icons.Default.Palette,
                    title = "Dark Mode",
                    subtitle =
                        "SYSTEM".replaceFirstChar { it.uppercase() },
                    checked = uiState.isDarkMode,
                    onCheckedChange = { checked ->
                        viewModel.changeTheme(
                            isDarkMode = checked,
                        )
                        onThemeChange()
                    },
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                SettingsSectionTitle("Account")
                SettingItem(
                    icon = Icons.Default.VerifiedUser,
                    title = "Profile",
                    subtitle = "Manage your profile information",
                    onClick = { /* TODO: Navigate to Profile Screen */ },
                )
                SettingItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Logout",
                    onClick = {
                        showLogoutConfirmDialog = true
                    },
                    titleColor = MaterialTheme.colorScheme.error,
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                SettingsSectionTitle("About")
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    subtitle = uiState.appVersion,
                    onClick = {},
                )
                SettingItem(
                    icon = Icons.Default.Policy,
                    title = "Privacy Policy",
                    onClick = { /* TODO: Open Privacy Policy URL */ },
                )
                SettingItem(
                    icon = Icons.Default.Terminal,
                    title = "Terms of Service",
                    onClick = { /* TODO: Open Terms of Service URL */ },
                )
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit,
) {
    ListItem(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick, role = Role.Button)
                .padding(horizontal = 8.dp),
        colors =
            ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        headlineContent = {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
                fontWeight = FontWeight.Normal,
            )
        },
        supportingContent =
            subtitle?.let {
                {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        },
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
    )
}

@Preview(showBackground = true, name = "Settings Screen Light")
@Composable
fun SettingScreenPreviewLight() {
    QuickFeedTheme(darkTheme = false) {
        Surface {
            SettingScreen(onNavigateBack = {})
        }
    }
}

@Preview(showBackground = true, name = "Settings Screen Dark")
@Composable
fun SettingScreenPreviewDark() {
    QuickFeedTheme(darkTheme = true) {
        Surface {
            SettingScreen(onNavigateBack = {})
        }
    }
}
