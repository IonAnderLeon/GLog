package com.example.glog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.glog.ui.state.HomeUiState
import com.example.glog.ui.viewmodels.HomeViewModel

// ui/screen/home/HomeScreen.kt

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onGameClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel() // Si usas Hilt para DI
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        modifier = modifier,
        uiState = uiState,
        onGameClick = onGameClick,
        onSearchTextChange = viewModel::onSearchTextChange,
        onToggleSearch = viewModel::onToggleSearch
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onGameClick: (String) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onToggleSearch: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeTopBar(
            showSearchBar = uiState.showSearchBar,
            searchText = uiState.searchText,
            onSearchTextChange = onSearchTextChange,
            onToggleSearch = onToggleSearch
        )

        HomeContentList(
            uiState = uiState,
            onGameClick = onGameClick
        )
    }
}

@Composable
private fun HomeTopBar(
    showSearchBar: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        // Título
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Barra de búsqueda
        if (showSearchBar) {
            HomeSearchBar(
                searchText = searchText,
                onSearchTextChange = onSearchTextChange,
                onToggleSearch = onToggleSearch
            )
        } else {
            HomeDefaultBar(onToggleSearch = onToggleSearch)
        }
    }
}

@Composable
private fun HomeSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onToggleSearch: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Buscar...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        IconButton(
            onClick = onToggleSearch,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cerrar búsqueda",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HomeDefaultBar(
    onToggleSearch: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onToggleSearch,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HomeContentList(
    uiState: HomeUiState,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(uiState.gameSections) { section ->
            GameSection(
                section = section,
                onGameClick = onGameClick
            )
        }
    }
}

@Composable
private fun GameSection(
    section: GameSection,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Título de la sección
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista horizontal de juegos
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(section.games) { game ->
                GameItem(
                    game = game,
                    onClick = { onGameClick(game.id) }
                )
            }
        }
    }
}

@Composable
private fun GameItem(
    game: GameItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(width = 150.dp, height = 100.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay para el título
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0.5f
                        )
                    )
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }
    }
}