package com.example.glog.data.mapper

import com.example.glog.data.network.dto.UserDTO
import com.example.glog.domain.model.User

class UserMapper {
    fun toEntity(dto: UserDTO): User {
        return User(
            id = dto.idUser ?: 0,
            nickname = dto.nickname?: "Usuario",
            image = dto.image
        )
    }
}