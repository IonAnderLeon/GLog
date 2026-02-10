package com.example.glog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.glog.R
import com.example.glog.domain.model.Game
import com.example.glog.ui.state.GameInfoUiState
import com.example.glog.ui.viewmodels.GameInfoViewModel

@Composable
fun GameInfoScreen(
    navController: NavController,
    viewModel: GameInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gameId = remember { navController.currentBackStackEntry?.arguments?.getString("id") }

    LaunchedEffect(gameId) {
        gameId?.toIntOrNull()?.let { viewModel.loadGame(it) }
    }

    GameInfoContent(
        uiState = uiState,
        onBack = { navController.navigateUp() },
        onRetry = { gameId?.toIntOrNull()?.let { viewModel.loadGame(it) } },
        onToggleFavorites = { viewModel.toggleFavorites() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameInfoContent(
    uiState: GameInfoUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleFavorites: () -> Unit = {}
) {
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    uiState.error?.let {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Reintentar") }
        }
        return
    }

    val game = uiState.game ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Botón de retroceso personalizado (opcional)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver"
            )
        }

        TopGameInfo(
            game = game,
            isInFavorites = uiState.isInFavorites,
            isUpdatingFavorites = uiState.isUpdatingFavorites,
            onToggleFavorites = onToggleFavorites
        )
        RatingRow(rating = game.rating ?: 0.0)
        DescriptionRow(description = game.description?.takeIf { it.isNotBlank() } ?: "Sin descripción disponible.")
    }
}

@Composable
private fun FavoritesChip(
    isInFavorites: Boolean,
    isUpdating: Boolean,
    onToggle: () -> Unit
) {
    AssistChip(
        onClick = { if (!isUpdating) onToggle() },
        label = {
            Text(
                text = if (isInFavorites) "Quitar de favoritos" else "Agregar a favoritos"
            )
        },
        leadingIcon = {
            Icon(
                imageVector = if (isInFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        enabled = !isUpdating
    )
}

@Composable
private fun TopGameInfo(
    game: Game,
    isInFavorites: Boolean = false,
    isUpdatingFavorites: Boolean = false,
    onToggleFavorites: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(275.dp)
            .padding(horizontal = 22.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = game.title ?: "Sin título",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "RELEASE DATE · ${game.releaseYear ?: "N/A"}")
            Text(text = "PLATFORM · ${game.platformName ?: "Desconocida"}")
            Text(text = "GENRE · ${game.genreName ?: "Desconocido"}")
            Spacer(modifier = Modifier.height(12.dp))
            FavoritesChip(
                isInFavorites = isInFavorites,
                isUpdating = isUpdatingFavorites,
                onToggle = onToggleFavorites
            )
        }

        AsyncImage(
            model = game.imageUrl ?: "",
            contentDescription = null,
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.placeholder)
        )
    }
}

@Composable
private fun RatingRow(rating: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        RatingColumn(rating = rating)
    }
}

@Composable
private fun RatingColumn(rating: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Gray,
            fontSize = 40.sp
        )
        StarRatingBar(
            rating = rating,
            maxStars = 5,
            starSize = 30.dp,
            activeColor = Color.Gray,
            inactiveColor = Color.DarkGray
        )
    }
}

@Composable
private fun DescriptionRow(description: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = 22.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = DividerDefaults.Thickness
        )

        Text(
            text = description,
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { expanded = !expanded }
        )

        Text(
            text = if (expanded) "Mostrar menos." else "Ver más...",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable { expanded = !expanded }
        )
    }
}

@Composable
fun StarRatingBar(
    rating: Double,
    maxStars: Int = 5,
    starSize: Dp = 32.dp,
    activeColor: Color = Color.Yellow,
    inactiveColor: Color = Color.Gray
) {
    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val starRating = when {
                rating >= i -> 1.0
                rating > i - 1 -> rating - (i - 1)
                else -> 0.0
            }

            StarIcon(
                fillRatio = starRating,
                size = starSize,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }
    }
}




@Composable
fun StarIcon(
    fillRatio: Double,
    size: Dp,
    activeColor: Color,
    inactiveColor: Color
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = inactiveColor,
            modifier = Modifier.size(size)
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = activeColor,
            modifier = Modifier
                .size(size)
                .drawWithContent {
                    drawIntoCanvas { canvas ->
                        canvas.save()

                        canvas.clipRect(
                            left = 0f,
                            top = 0f,
                            right = size.toPx() * fillRatio.toFloat(),
                            bottom = size.toPx()
                        )
                        drawContent()
                        canvas.restore()
                    }
                }
        )
    }
}