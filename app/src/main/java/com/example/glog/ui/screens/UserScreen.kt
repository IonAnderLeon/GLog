package com.example.glog.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.glog.ui.components.MarqueeText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.glog.R
import com.example.glog.domain.model.Game
import com.example.glog.domain.model.User
import com.example.glog.data.preferences.AppPreferences
import com.example.glog.ui.navigation.Destination
import com.example.glog.ui.state.UserUiState
import com.example.glog.ui.viewmodels.AppPreferencesViewModel
import com.example.glog.ui.viewmodels.UserStats
import com.example.glog.ui.viewmodels.UserViewModel

@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel(),
    appPrefsViewModel: AppPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appPrefs by appPrefsViewModel.preferences.collectAsStateWithLifecycle(initialValue = AppPreferences())
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showChangeNicknameDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    UserContent(
        uiState = uiState,
        navController = navController,
        onSettingsClick = { showSettingsDialog = true }
    )

    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { showSettingsDialog = false },
            useDarkTheme = appPrefs.useDarkTheme ?: isSystemInDarkTheme(),
            onDarkThemeChange = { appPrefsViewModel.setDarkTheme(it) },
            useLargeText = appPrefs.useLargeText,
            onLargeTextChange = { newValue ->
                showSettingsDialog = false
                scope.launch {
                    delay(150)
                    appPrefsViewModel.setLargeText(newValue)
                }
            },
            onChangeNicknameClick = {
                showSettingsDialog = false
                showChangeNicknameDialog = true
            }
        )
    }

    if (showChangeNicknameDialog) {
        ChangeNicknameDialog(
            currentNickname = uiState.user?.nickname ?: "",
            onDismiss = { showChangeNicknameDialog = false },
            onConfirm = { newNickname ->
                viewModel.updateNickname(newNickname)
                showChangeNicknameDialog = false
            }
        )
    }
}

@Composable
private fun UserContent(
    uiState: UserUiState,
    navController: NavController,
    onSettingsClick: () -> Unit = {}
) {
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
                item { UserHeader(user = user, onSettingsClick = onSettingsClick) }
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
                Text(
                    "No hay datos de usuario",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun UserHeader(user: User, onSettingsClick: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "settings_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }

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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
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
    onGameClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Favoritos",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
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

@Composable
private fun StatsSection(stats: UserStats) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Estadísticas",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = "${stats.playTimeHours}h",
                label = "Horas totales"
            )

            StatItem(
                value = stats.distinctGames.toString(),
                label = "Juegos distintos"
            )

        }

        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
        StatItem(
            value = stats.favoritePlatform,
            label = "Plataforma fav"
        )}
        DividerSection()
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
        Text("Welcome Gamer!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)}
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(200.dp)
    ) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = label,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}