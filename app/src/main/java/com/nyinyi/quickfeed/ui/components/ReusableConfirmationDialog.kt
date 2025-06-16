package com.nyinyi.quickfeed.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme

@Composable
fun ReusableConfirmationDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    dismissButtonColor: Color = MaterialTheme.colorScheme.primary,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = { Text(text = text) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismissRequest()
                    },
                ) {
                    Text(confirmButtonText, color = confirmButtonColor)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(dismissButtonText, color = dismissButtonColor)
                }
            },
        )
    }
}

@Preview(showBackground = true, name = "Confirmation Dialog Preview")
@Composable
fun ReusableConfirmationDialogPreview() {
    QuickFeedTheme {
        ReusableConfirmationDialog(
            showDialog = true,
            onDismissRequest = {},
            onConfirm = {},
            title = "Confirm Action",
            text = "Are you sure you want to perform this action?",
            confirmButtonText = "Yes, Proceed",
            dismissButtonText = "No, Go Back",
            confirmButtonColor = MaterialTheme.colorScheme.error,
        )
    }
}

@Preview(showBackground = true, name = "Logout Dialog Preview")
@Composable
fun LogoutConfirmationDialogPreview() {
    QuickFeedTheme {
        ReusableConfirmationDialog(
            showDialog = true,
            onDismissRequest = {},
            onConfirm = {},
            title = "Confirm Logout",
            text = "Are you sure you want to logout?",
            confirmButtonColor = MaterialTheme.colorScheme.error,
        )
    }
}
