package com.example.glog.domain.model

data class Game(
    val id: Int,                    // Non-null
    val title: String?,         // Nullable
    val imageUrl: String?,      // Nullable
    val releaseYear: Int?,      // Nullable
    val rating: Double?,         // Nullable
    val platformName: String?,  // Nullable
    val genreName: String?     // Nullable
)
