package com.example.glog.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.glog.R
import com.example.glog.domain.model.Collection
import com.example.glog.domain.model.Game
import com.example.glog.ui.state.CollectionEvent
import com.example.glog.ui.state.CollectionState
import com.example.glog.ui.viewmodels.CollectionViewModel
import com.example.glog.ui.viewmodels.GameSearchViewModel

@Composable
fun Collection(
    modifier: Modifier = Modifier,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(CollectionEvent.LoadCollections)
    }

    if (state.selectedCollection != null) {
        val gameSearchViewModel: GameSearchViewModel = hiltViewModel()
        CollectionInfoScreen(
            collection = state.selectedCollection!!,
            onBack = { viewModel.onEvent(CollectionEvent.SelectCollection(null)) },
            onUpdateCollection = { viewModel.onEvent(CollectionEvent.UpdateCollection(it)) },
            onAddGames = { games ->
                viewModel.onEvent(CollectionEvent.AddGamesToCollection(state.selectedCollection!!, games))
            },
            gameSearchViewModel = gameSearchViewModel
        )
    } else {
        CollectionListContent(modifier = modifier, state = state) { collection ->
            viewModel.onEvent(CollectionEvent.SelectCollection(collection))
        }
    }
}

@Composable
private fun CollectionListContent(
    modifier: Modifier,
    state: CollectionState,
    onCollectionClick: (Collection) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Collections",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        state.error?.let { errorMsg ->
            Text(
                text = "Error: $errorMsg",
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.collections) { collection ->
                        CollectionCard(
                            collection = collection,
                            onClick = { onCollectionClick(collection) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionCard(
    collection: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrl = collection.games.firstOrNull()?.imageUrl?.takeIf { it.isNotBlank() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder)
                )
            }
            Text(
                text = collection.name.ifBlank { "Sin nombre" },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CollectionInfoScreen(
    collection: Collection,
    onBack: () -> Unit,
    onUpdateCollection: (Collection) -> Unit,
    onAddGames: (List<Game>) -> Unit,
    gameSearchViewModel: GameSearchViewModel
) {
    var isEditingName by remember(collection.id) { mutableStateOf(false) }
    var editedName by remember(collection.id) { mutableStateOf(collection.name) }
    var showAddGameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(collection) {
        editedName = collection.name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            if (isEditingName) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(
                    onClick = {
                        val name = editedName.trim().ifBlank { collection.name }
                        onUpdateCollection(collection.copy(name = name))
                        editedName = name
                        isEditingName = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Guardar"
                    )
                }
            } else {
                Text(
                    text = collection.name.ifBlank { "Colección" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { isEditingName = true; editedName = collection.name }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar nombre"
                    )
                }
            }
        }

        collection.description?.takeIf { it.isNotBlank() }?.let { desc ->
            Text(
                text = desc,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "Juegos (${collection.games.size})",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                AddGameCard(onClick = { showAddGameDialog = true })
            }
            items(collection.games) { game: Game ->
                    GameInCollectionCard(game = game)
                }
        }
    }

    if (showAddGameDialog) {
        AddGameToCollectionDialog(
            onDismiss = {
                gameSearchViewModel.clearSearch()
                showAddGameDialog = false
            },
            gameSearchViewModel = gameSearchViewModel,
            existingGameIds = collection.games.map { it.id }.toSet(),
            onAdd = { game ->
                onAddGames(listOf(game))
                gameSearchViewModel.clearSearch()
                showAddGameDialog = false
            }
        )
    }
}

@Composable
private fun AddGameCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(150f / 200f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir juego",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddGameToCollectionDialog(
    onDismiss: () -> Unit,
    gameSearchViewModel: GameSearchViewModel,
    existingGameIds: Set<Int>,
    onAdd: (Game) -> Unit
) {
    var gameSearchQuery by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    val searchResults by gameSearchViewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by gameSearchViewModel.isSearching.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir juego a la colección") },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = if (selectedGame != null) selectedGame!!.title ?: "" else gameSearchQuery,
                    onValueChange = {
                        gameSearchQuery = it
                        selectedGame = null
                        gameSearchViewModel.searchGames(it)
                    },
                    label = { Text("Buscar juego") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.height(28.dp))
                    }
                }
                if (searchResults.isNotEmpty() && selectedGame == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(
                                searchResults
                                    .take(10)
                                    .filter { it.id !in existingGameIds }
                            ) { game: Game ->
                                Text(
                                    text = game.title ?: "Sin título",
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
                    selectedGame?.let { onAdd(it) }
                }
            ) {
                Text("Añadir", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@Composable
private fun GameInCollectionCard(
    game: Game,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(150f / 200f)
    ) {
        AsyncImage(
            model = game.imageUrl?.takeIf { it.isNotBlank() },
            contentDescription = game.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.7f))
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
        ) {
            Text(
                text = game.title?.ifBlank { "Sin título" } ?: "Sin título",
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
