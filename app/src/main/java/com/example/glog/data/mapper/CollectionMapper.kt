package com.example.glog.data.mapper

import com.example.glog.data.network.dto.CollectionDTO
import com.example.glog.data.network.dto.CollectionGamesDTO
import com.example.glog.domain.model.Collection

class CollectionMapper(
    private val gameMapper: GameMapper
) {
    fun toEntity(dto: CollectionGamesDTO): Collection {
        val games = dto.games?.mapNotNull { it?.let { gameMapper.toEntity(it) } } ?: emptyList()
        return Collection(
            id = dto.collection?.idCollection ?: 0,
            name = dto.collection?.name ?: "",
            description = dto.collection?.description,
            gameIds = games.map { it.id.toString() },
            games = games
        )
    }

    fun toDto(entity: Collection): CollectionDTO {
        return CollectionDTO(
            idCollection = entity.id,
            name = entity.name,
            description = entity.description
        )
    }
}
