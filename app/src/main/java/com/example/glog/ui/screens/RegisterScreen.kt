package com.example.glog.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.glog.R
import com.example.glog.domain.model.Game
import com.example.glog.domain.model.Register
import com.example.glog.ui.state.RegisterEvent
import com.example.glog.ui.state.RegisterState
import com.example.glog.ui.viewmodels.GameSearchViewModel
import com.example.glog.ui.viewmodels.RegisterViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    gameSearchViewModel: GameSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(RegisterEvent.LoadRegisters)
    }

    RegisterContent(
        modifier = modifier,
        state = state,
        viewModel = viewModel,
        gameSearchViewModel = gameSearchViewModel
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RegisterContent(
    modifier: Modifier,
    state: RegisterState,
    viewModel: RegisterViewModel,
    gameSearchViewModel: GameSearchViewModel
) {
    var expandedMenuId by remember { mutableStateOf<Int?>(null) }
    var registerToEdit by remember { mutableStateOf<Register?>(null) }
    var registerToDelete by remember { mutableStateOf<Register?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        state.error?.let { errorMsg ->
            Text(
                text = stringResource(R.string.error_message, errorMsg),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 14.sp
            )
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                var expandedIds by remember { mutableStateOf(setOf<Int>()) }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        items(state.registers) { register ->
                            Box(modifier = Modifier.fillMaxWidth()) {
                                RegisterCard(
                                    register = register,
                                    isExpanded = register.id in expandedIds,
                                    onToggle = {
                                        expandedIds = if (register.id in expandedIds) {
                                            expandedIds - register.id
                                        } else {
                                            expandedIds + register.id
                                        }
                                    },
                                    onLongPress = { expandedMenuId = register.id }
                                )
                                DropdownMenu(
                                    expanded = expandedMenuId == register.id,
                                    onDismissRequest = { expandedMenuId = null }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.register_edit)) },
                                        onClick = {
                                            expandedMenuId = null
                                            registerToEdit = register
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.register_delete)) },
                                        onClick = {
                                            expandedMenuId = null
                                            registerToDelete = register
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    registerToDelete?.let { register ->
        ConfirmDeleteRegisterDialog(
            register = register,
            onDismiss = { registerToDelete = null },
            onConfirm = {
                viewModel.onEvent(RegisterEvent.DeleteRegister(register))
                registerToDelete = null
            }
        )
    }

    registerToEdit?.let { register ->
        EditRegisterDialog(
            register = register,
            gameSearchViewModel = gameSearchViewModel,
            onDismiss = {
                gameSearchViewModel.clearSearch()
                registerToEdit = null
            },
            onSave = { date, playtime, gameId, gameName, gameImageUrl ->
                viewModel.onEvent(
                    RegisterEvent.UpdateRegister(
                        register = register,
                        date = date,
                        playtime = playtime,
                        gameId = gameId,
                        gameName = gameName,
                        gameImageUrl = gameImageUrl
                    )
                )
                gameSearchViewModel.clearSearch()
                registerToEdit = null
            }
        )
    }
}

@Composable
private fun RegisterCard(
    register: Register,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .combinedClickable(
                onClick = onToggle,
                onLongClick = onLongPress
            )
            .animateContentSize(animationSpec = tween(200)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 64.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = register.gameImageUrl?.takeIf { it.isNotBlank() },
                        contentDescription = register.gameName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.placeholder)
                    )
                }

                Text(
                    text = register.gameName?.ifBlank { stringResource(R.string.no_game) } ?: stringResource(R.string.no_game),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, end = 8.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand),
                    modifier = Modifier.size(32.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(200)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.time_format, register.playtime?.let { "${it}h" } ?: stringResource(R.string.dash)),
                        modifier = Modifier.wrapContentWidth(),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Visible
                    )
                    Text(
                        text = stringResource(R.string.date_format, register.date?.ifBlank { stringResource(R.string.dash) } ?: stringResource(R.string.dash)),
                        modifier = Modifier.wrapContentWidth(),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Visible
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteRegisterDialog(
    register: Register,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val gameName = register.gameName?.takeIf { it.isNotBlank() } ?: stringResource(R.string.no_game)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_register_title)) },
        text = {
            Text(stringResource(R.string.delete_register_confirmation, gameName))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditRegisterDialog(
    register: Register,
    gameSearchViewModel: GameSearchViewModel,
    onDismiss: () -> Unit,
    onSave: (date: String?, playtime: Double?, gameId: Int?, gameName: String?, gameImageUrl: String?) -> Unit
) {
    var date by remember(register.id) { mutableStateOf(register.date ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var playtimeStr by remember(register.id) { mutableStateOf(register.playtime?.toString() ?: "") }
    var gameSearchQuery by remember(register.id) { mutableStateOf("") }
    var selectedGame by remember(register.id) {
        mutableStateOf<Game?>(
            if (register.gameId != null && (register.gameName != null || register.gameImageUrl != null))
                Game(
                    id = register.gameId!!,
                    title = register.gameName,
                    imageUrl = register.gameImageUrl,
                    releaseYear = null,
                    rating = null,
                    platformName = null,
                    genreName = null,
                    description = null
                )
            else null
        )
    }
    val searchResults by gameSearchViewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by gameSearchViewModel.isSearching.collectAsStateWithLifecycle()

    LaunchedEffect(gameSearchQuery) {
        if (gameSearchQuery.isNotBlank()) gameSearchViewModel.searchGames(gameSearchQuery)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_register_title)) },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text(stringResource(R.string.field_date)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = playtimeStr,
                    onValueChange = { playtimeStr = it },
                    label = { Text(stringResource(R.string.field_playtime_optional)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = if (selectedGame != null) selectedGame!!.title ?: "" else gameSearchQuery,
                    onValueChange = {
                        gameSearchQuery = it
                        selectedGame = null
                        gameSearchViewModel.searchGames(it)
                    },
                    label = { Text(stringResource(R.string.field_game_search)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.height(28.dp))
                    }
                }
                if (searchResults.isNotEmpty() && selectedGame == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(searchResults.take(10)) { game ->
                                Text(
                                    text = game.title ?: stringResource(R.string.no_title),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedGame = game
                                            gameSearchQuery = ""
                                            gameSearchViewModel.clearSearch()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val playtime = playtimeStr.toDoubleOrNull()
                    val gameId = selectedGame?.id
                    val gameName = selectedGame?.title
                    val gameImageUrl = selectedGame?.imageUrl
                    onSave(
                        date.ifBlank { null },
                        playtime,
                        gameId,
                        gameName,
                        gameImageUrl
                    )
                }
            ) {
                Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}
