package com.example.glog.domain.model

data class Collection(
    val id: Int,
    val name: String,
    val description: String?,
    val gameIds: List<String> = emptyList(),
    val games: List<Game> = emptyList()
)
