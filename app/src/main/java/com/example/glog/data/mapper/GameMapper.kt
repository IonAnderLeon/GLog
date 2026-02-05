package com.example.glog.data.mapper

//import com.example.glog.data.network.dto.GameDTO
//import com.example.glog.domain.model.Game
//
////Ejemplo a modificar mas adelante
//class GameMapper {
//    fun toEntity(dto: GameDTO): Game {
//        return Game(
//            id_game = dto.id.toString(),
//            name = dto.name,
//            releaseDate = dto.year),
//            platforms = emptyList() // Aquí otro mapper buscaría los objetos completos
//        )
//    }
//
//    fun toDto(entity: Game): GameDto {
//        return GameDto(
//            id = entity.id.toLong(),
//            title = entity.title,
//            release_date = entity.releaseDate.toString(),
//            platform_ids = entity.platforms.map { it.id.toLong() }
//        )
//    }
//}