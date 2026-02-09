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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.glog.R
import com.example.glog.domain.model.Game
import com.example.glog.domain.model.User
import com.example.glog.ui.navigation.Destination
import com.example.glog.ui.state.UserUiState
import com.example.glog.ui.viewmodels.UserStats
import com.example.glog.ui.viewmodels.UserViewModel

@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    UserContent(
        uiState = uiState,
        navController= navController
    )
}

@Composable
private fun UserContent(uiState: UserUiState, navController: NavController) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* TODO: Retry logic */ }) {
                    Text("Reintentar")
                }
            }
        }

        uiState.user != null -> {
            val user = uiState.user
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { UserHeader(user = user) }
                item { DividerSection() }
                item { FavoriteGamesSection(
                    games = uiState.favoriteGames,
                    onGameClick = { gameId ->
                        navController.navigate(Destination.GameDetails.createRoute(gameId.toString()))
                    }
                )
                }
                item { DividerSection() }
                item { StatsSection(stats = uiState.stats) }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay datos de usuario")
            }
        }
    }
}

@Composable
private fun UserHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = user.image ?: "",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder)
            )

            Text(
                text = user.nickname ?: "Usuario",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DividerSection() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
    )
}

@Composable
private fun FavoriteGamesSection(
    games: List<Game>,
    onGameClick: (Int) -> Unit  // Añade este parámetro
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Fav Collection",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(games) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClick(game.id) }  // Usa el callback
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(150.dp, 200.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = game.imageUrl ?: "",
            contentDescription = game.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )

        Text(
            text = game.title ?: "Sin título",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.7f))
                .fillMaxWidth()
                .padding(4.dp),
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatsSection(stats: UserStats) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Estadísticas",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = "${stats.playTimeHours}h",
                label = "Últimas 2 semanas"
            )

            StatItem(
                value = stats.distinctGames.toString(),
                label = "Juegos distintos"
            )

            StatItem(
                value = stats.favoritePlatform,
                label = "Plataforma fav"
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(135.dp)
    ) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}