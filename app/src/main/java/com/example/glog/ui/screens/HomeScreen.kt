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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.glog.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.glog.domain.model.Game
import com.example.glog.ui.components.MarqueeText
import com.example.glog.ui.navigation.Destination
import com.example.glog.ui.state.HomeUiState
import com.example.glog.ui.viewmodels.GameSearchViewModel
import com.example.glog.ui.viewmodels.HomeViewModel


@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    searchViewModel: GameSearchViewModel = hiltViewModel()
) {
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by searchViewModel.isSearching.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.loadGames()
    }

    HomeContent(
        homeState = homeState,
        searchResults = searchResults,
        isSearching = isSearching,
        onGameClick = { gameId ->
            navController.navigate(Destination.GameDetails.createRoute(gameId.toString()))
        },
        onSearchTextChange = { query ->
            homeViewModel.onSearchTextChange(query)
            searchViewModel.searchGames(query)
        },
        onToggleSearch = {
            homeViewModel.onToggleSearch()
            if (!homeState.showSearchBar) {
                searchViewModel.clearSearch()
            }
        }
    )
}
@Composable
private fun HomeContent(
    homeState: HomeUiState,
    searchResults: List<Game>,
    isSearching: Boolean,
    onGameClick: (Int) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onToggleSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Home",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        homeState.error?.let { errorMsg ->
            Text(
                text = "Error: $errorMsg",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 14.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (homeState.showSearchBar) {
                OutlinedTextField(
                    value = homeState.searchText,
                    onValueChange = onSearchTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar...") },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    }
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            IconButton(onClick = onToggleSearch) {
                Icon(
                    imageVector = if (homeState.showSearchBar) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (homeState.showSearchBar) "Cerrar búsqueda" else "Buscar"
                )
            }
        }

        // Mostrar resultados de búsqueda si hay texto
        if (homeState.showSearchBar && homeState.searchText.isNotBlank()) {
            SearchResultsContent(
                searchResults = searchResults,
                isSearching = isSearching,
                onGameClick = onGameClick
            )
        } else {
            HomeSectionsContent(
                homeState = homeState,
                onGameClick = onGameClick
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchResults: List<Game>,
    isSearching: Boolean,
    onGameClick: (Int) -> Unit
) {
    when {
        isSearching -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        searchResults.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontraron juegos")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(searchResults) { game ->
                    SearchResultItem(
                        game = game,
                        onClick = { onGameClick(game.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    game: Game,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title ?: "Sin título",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${game.platformName ?: ""} • ${game.genreName ?: ""}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Año: ${game.releaseYear ?: "N/A"} • Rating: ${game.rating ?: "N/A"}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HomeSectionsContent(
    homeState: HomeUiState,
    onGameClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GameSection(
                title = "Recientes",
                games = homeState.recentGames,
                onGameClick = onGameClick
            )
        }

        item {
            GameSection(
                title = "Populares",
                games = homeState.popularGames,
                onGameClick = onGameClick
            )
        }

        item {
            GameSection(
                title = "Juegos de PC",
                games = homeState.pcGames,
                onGameClick = onGameClick
            )
        }

        item {
            GameSection(
                title = "Juegos de género Aventura",
                games = homeState.adventureGames,
                onGameClick = onGameClick
            )
        }
    }
}



@Composable
private fun GameSection(
    title: String,
    games: List<Game>,
    onGameClick: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(games) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClick(game.id) }
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: Game,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(150.dp, 200.dp)
            .padding(4.dp)
            .clickable(onClick = onClick)
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
                .clipToBounds()
        ) {
            MarqueeText(
                text = game.title ?: "Sin título",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}