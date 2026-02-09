package com.example.glog.data.mapper

import com.example.glog.data.network.dto.RegisterDTO
import com.example.glog.data.network.dto.RegisterDetailDTO
import com.example.glog.domain.model.Register

class RegisterMapper {
    fun toEntity(detailDto: RegisterDetailDTO): Register {
        return Register(
            id = detailDto.register?.idRegister ?: 0,
            date = detailDto.register?.date ?: "",
            playtime = detailDto.register?.playtime ?: 0.0,
            gameId = detailDto.register?.idGame ?: 0,
            gameName = detailDto.gameName ?: "",
            gameImageUrl = detailDto.gameImageUrl?.takeIf { it.isNotBlank() },
            userId = detailDto.register?.idUsuario ?: 0,
            userName = detailDto.userName ?: ""
        )
    }

    fun toDto(entity: Register): RegisterDTO {
        return RegisterDTO(
            idRegister = entity.id,
            date = entity.date,
            idGame = entity.gameId,
            idUsuario = entity.userId,
            playtime = entity.playtime
        )
    }
}