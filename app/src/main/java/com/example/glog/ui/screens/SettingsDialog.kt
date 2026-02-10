package com.example.glog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.glog.ui.components.AnimatedSwitch

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    useDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    useLargeText: Boolean,
    onLargeTextChange: (Boolean) -> Unit,
    onChangeNicknameClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tema oscuro",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    AnimatedSwitch(
                        isOn = useDarkTheme,
                        onToggle = onDarkThemeChange
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onChangeNicknameClick),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cambiar nickname",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Letra más grande",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    AnimatedSwitch(
                        isOn = useLargeText,
                        onToggle = onLargeTextChange
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun ChangeNicknameDialog(
    currentNickname: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nickname by remember(currentNickname) { mutableStateOf(currentNickname) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar nickname", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nickname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(nickname.trim())
                    onDismiss()
                }
            ) {
                Text("Guardar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}
