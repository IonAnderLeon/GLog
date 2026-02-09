package com.example.glog.ui.screens

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import kotlin.math.roundToInt
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.glog.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.glog.domain.model.Game
import com.example.glog.ui.navigation.Destination
import com.example.glog.ui.state.HomeUiState
import com.example.glog.ui.viewmodels.HomeViewModel


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }

    HomeContent(
        uiState = uiState,
        onGameClick = { gameId ->
            navController.navigate(Destination.GameDetails.createRoute(gameId.toString()))
        },
        onSearchTextChange = viewModel::onSearchTextChange,
        onToggleSearch = viewModel::onToggleSearch
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
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
            fontWeight = FontWeight.Bold
        )

        uiState.error?.let { errorMsg ->
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
            if (uiState.showSearchBar) {
                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = onSearchTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar...") }
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            IconButton(onClick = onToggleSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GameSection(
                    title = "Recientes",
                    games = uiState.recentGames,
                    onGameClick = onGameClick
                )
            }

            item {
                GameSection(
                    title = "Populares",
                    games = uiState.popularGames,
                    onGameClick = onGameClick
                )
            }
            item {
                GameSection(
                    title = "Juegos de PC",
                    games = uiState.popularGames,
                    onGameClick = onGameClick
                )
            }
            item {
                GameSection(
                    title = "Juegos de Switch",
                    games = uiState.popularGames,
                    onGameClick = onGameClick
                )
            }
            item {
                GameSection(
                    title = "Juegos de Estrategia",
                    games = uiState.popularGames,
                    onGameClick = onGameClick
                )
            }

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
            fontWeight = FontWeight.Bold
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

@Composable
private fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    color: Color = Color.White
) {
    var textWidthPx by remember { mutableIntStateOf(0) }
    var boxWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val gapPx = with(density) { 32.dp.roundToPx() }
    val needMarquee = textWidthPx > 0 && boxWidthPx > 0 && textWidthPx > boxWidthPx

    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -(textWidthPx + gapPx).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000,
                easing = LinearEasing,
                delayMillis = 0),
            repeatMode = RepeatMode.Restart
        ),
        label = "marqueeOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { boxWidthPx = it.width }
            .clipToBounds()
    ) {
        when {
            textWidthPx == 0 -> {
                UnboundedMeasureLayout(
                    onMeasuredWidth = { textWidthPx = it }
                ) {
                    Text(
                        text = text,
                        fontSize = fontSize,
                        color = color,
                        maxLines = 1
                    )
                }
            }
            needMarquee -> {
                MarqueeScrollLayout(
                    scrollOffsetPx = offset.roundToInt()
                ) {
                    Row {
                        Text(
                            text = text,
                            fontSize = fontSize,
                            color = color,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        Text(
                            text = text,
                            fontSize = fontSize,
                            color = color,
                            maxLines = 1
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = text,
                    fontSize = fontSize,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Layout que dibuja su hijo (Row con dos textos) con ancho ilimitado y lo coloca con [scrollOffsetPx].
 * Así el Row no se recorta por el ancho de la card y al hacer scroll se ve todo el texto hasta el final.
 */
@Composable
private fun MarqueeScrollLayout(
    scrollOffsetPx: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier.fillMaxWidth(),
        content = content
    ) { measurables, constraints ->
        val unboundedConstraints = Constraints(
            minWidth = 0,
            maxWidth = Int.MAX_VALUE,
            minHeight = 0,
            maxHeight = constraints.maxHeight
        )
        val placeable = measurables.first().measure(unboundedConstraints)
        layout(constraints.maxWidth, placeable.height) {
            placeable.place(scrollOffsetPx, 0)
        }
    }
}

/**
 * Layout que mide su hijo con ancho ilimitado para obtener el ancho real del contenido
 * (p. ej. texto largo sin recortar). Así podemos saber si hace falta marquesina.
 */
@Composable
private fun UnboundedMeasureLayout(
    onMeasuredWidth: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            Box(Modifier.onSizeChanged { onMeasuredWidth(it.width) }) {
                content()
            }
        }
    ) { measurables, constraints ->
        val unboundedConstraints = Constraints(
            minWidth = 0,
            maxWidth = Int.MAX_VALUE,
            minHeight = 0,
            maxHeight = constraints.maxHeight
        )
        val placeable = measurables.first().measure(unboundedConstraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}