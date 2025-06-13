package com.nyinyi.quickfeed.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class DialogState(
    val show: Boolean = false,
    val title: String = "",
    val messageString: String = "",
    val isError: Boolean = false,
)

@Composable
fun StatusDialog(
    dialogState: DialogState,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = dialogState.title, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Text(
                text = dialogState.messageString,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
        containerColor = if (dialogState.isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
        titleContentColor = if (dialogState.isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        textContentColor = if (dialogState.isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
