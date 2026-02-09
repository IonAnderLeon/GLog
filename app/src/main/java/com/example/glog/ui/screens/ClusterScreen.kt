package com.example.glog.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.glog.ui.screens.components.DraggableFAB
import com.example.glog.ui.screens.components.FabPosition
import com.example.glog.ui.state.CollectionEvent
import com.example.glog.ui.state.RegisterEvent
import com.example.glog.domain.model.Game
import com.example.glog.ui.viewmodels.CollectionViewModel
import com.example.glog.ui.viewmodels.GameSearchViewModel
import com.example.glog.ui.viewmodels.RegisterViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClusterScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    collectionViewModel: CollectionViewModel = hiltViewModel(),
    registerViewModel: RegisterViewModel = hiltViewModel(),
    gameSearchViewModel: GameSearchViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var showCreateRegisterDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Cluster",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Absolute.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Colecciones",
                        fontSize = 18.sp,
                        fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (pagerState.currentPage == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // Registro tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Registros",
                        fontSize = 18.sp,
                        fontWeight = if (pagerState.currentPage == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (pagerState.currentPage == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (pagerState.currentPage == 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Pager con las 2 pestañas y FAB arrastrable encima
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val initialFabPosition = remember {
                FabPosition(
                    x = maxWidth - 56.dp - 24.dp,
                    y = maxHeight - 56.dp - 24.dp
                )
            }
            var fabPosition by remember { mutableStateOf(initialFabPosition) }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page: Int ->
                when (page) {
                    0 -> Collection(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                    1 -> RegisterScreen(modifier = Modifier.fillMaxSize())
                }
            }

            DraggableFAB(
                position = fabPosition,
                onPositionChange = { fabPosition = it },
                onClick = {
                    val page = pagerState.currentPage
                    val offset = pagerState.currentPageOffsetFraction
                    val inMiddleTowards1 = page == 0 && offset < -0.25f
                    val inMiddleTowards0 = page == 1 && offset > 0.25f
                    when {
                        inMiddleTowards1 -> coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                        inMiddleTowards0 -> coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                        page == 0 -> showCreateCollectionDialog = true
                        else -> showCreateRegisterDialog = true
                    }
                }
            )
        }
    }

    if (showCreateCollectionDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateCollectionDialog = false },
            onCreate = { name, description ->
                collectionViewModel.onEvent(CollectionEvent.CreateCollection(name, description))
                showCreateCollectionDialog = false
            }
        )
    }
    if (showCreateRegisterDialog) {
        CreateRegisterDialog(
            onDismiss = {
                gameSearchViewModel.clearSearch()
                showCreateRegisterDialog = false
            },
            gameSearchViewModel = gameSearchViewModel,
            onCreate = { date, playtime, gameId ->
                registerViewModel.onEvent(
                    RegisterEvent.CreateRegister(
                        date = date,
                        playtime = playtime,
                        gameId = gameId,
                        userId = 1
                    )
                )
                gameSearchViewModel.clearSearch()
                showCreateRegisterDialog = false
            }
        )
    }
}

@Composable
private fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva colección") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) onCreate(name, description.ifBlank { null })
                }
            ) {
                Text("Crear", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CreateRegisterDialog(
    onDismiss: () -> Unit,
    gameSearchViewModel: GameSearchViewModel,
    onCreate: (date: String?, playtime: Double?, gameId: Int?) -> Unit
) {
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    var date by remember { mutableStateOf(today) }
    var playtimeStr by remember { mutableStateOf("") }
    var gameSearchQuery by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    val searchResults by gameSearchViewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by gameSearchViewModel.isSearching.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo registro") },
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
                    label = { Text("Fecha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = playtimeStr,
                    onValueChange = { playtimeStr = it },
                    label = { Text("Tiempo jugado (opcional)") },
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
                    label = { Text("Juego (buscar por nombre)") },
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
                            items(searchResults.take(10)) { game: Game ->
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
                    val playtime = playtimeStr.toDoubleOrNull()
                    val gameId = selectedGame?.id
                    onCreate(
                        date.ifBlank { null },
                        playtime,
                        gameId
                    )
                }
            ) {
                Text("Crear", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}
