package com.example.glog.data.mapper

import com.example.glog.data.network.dto.CollectionDTO
import com.example.glog.data.network.dto.CollectionGamesDTO

class CollectionMapper(
    private val gameMapper: GameMapper
) {
//    fun toEntity(dto: CollectionGamesDTO): domain.model.Collection {
//        return domain.model.Collection(
//            id = dto.collection?.idCollection.toString(),
//            name = dto.collection?.name ?: "",
//            description = dto.collection?.description,
//            games = dto.games?.map { gameMapper.toEntity(it) } ?: emptyList()
//        )
//    }
//
//    fun toDto(entity: domain.model.Collection): CollectionDTO {
//        return CollectionDTO(
//            idCollection = entity.id.toIntOrNull(),
//            name = entity.name,
//            description = entity.description,
//            gameIds = entity.games.mapNotNull { it.id.toIntOrNull() }
//        )
//    }
}