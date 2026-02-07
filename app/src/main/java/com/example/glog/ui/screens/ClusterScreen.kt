package com.example.glog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ClusterScreen(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

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

        // Pager con las 2 pestañas
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> Collection(modifier = Modifier.fillMaxSize())
                1 -> RegisterScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
