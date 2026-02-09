package com.example.glog.data.mapper

import com.example.glog.data.mapper.constants.Utils.formatEmpty
import com.example.glog.data.mapper.constants.Utils.formatGenre
import com.example.glog.data.mapper.constants.Utils.formatPlatform
import com.example.glog.data.network.dto.GameDTO
import com.example.glog.data.network.dto.GameDetailDTO
import com.example.glog.domain.model.Game

class GameMapper {

    fun toEntity(dto: GameDetailDTO): Game {
        return Game(
            id = dto.game?.id ?: 0,
            title = dto.game?.name.formatEmpty("Sin título"),
            imageUrl = dto.game?.image.takeIf { !it.isNullOrBlank() },
            releaseYear = dto.game?.year,
            rating = dto.game?.rating ?: 0.0,
            // Solo IDs como strings
            platformName = dto.platformName.formatEmpty("Plataforma desconocida"),
            genreName = dto.genreName.formatEmpty("Género desconocido")
        )
    }

    /** Para listados dentro de colecciones (GameDTO tiene menos campos que GameDetailDTO). */
    fun toEntity(dto: GameDTO): Game {
        return Game(
            id = dto.id ?: 0,
            title = dto.name.formatEmpty("Sin título"),
            imageUrl = dto.image.takeIf { !it.isNullOrBlank() },
            releaseYear = dto.year,
            rating = dto.rating ?: 0.0,
            platformName = dto.platformId.formatPlatform(),
            genreName = dto.genreId.formatGenre()
        )
    }
}