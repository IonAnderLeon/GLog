package com.example.glog.data.mapper

import com.example.glog.data.mapper.constants.Utils.formatEmpty
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

}