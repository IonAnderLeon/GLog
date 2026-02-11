package com.example.glog.data.mapper

import com.example.glog.data.network.dto.UserDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UserMapperTest {

    private lateinit var mapper: UserMapper

    @Before
    fun setUp() {
        mapper = UserMapper()
    }

    @Test
    fun toEntity_mapsUserDTOCorrectly() {
        val dto = UserDTO(idUser = 1, nickname = "GamerPro", image = "https://avatar.com/1.png")
        val user = mapper.toEntity(dto)

        assertEquals(1, user.id)
        assertEquals("GamerPro", user.nickname)
        assertEquals("https://avatar.com/1.png", user.image)
    }

    @Test
    fun toEntity_nullNickname_usesUsuario() {
        val dto = UserDTO(idUser = 2, nickname = null, image = null)
        val user = mapper.toEntity(dto)

        assertEquals(2, user.id)
        assertEquals("Usuario", user.nickname)
        assertNull(user.image)
    }

    @Test
    fun toEntity_nullId_usesZero() {
        val dto = UserDTO(idUser = null, nickname = "Test", image = null)
        val user = mapper.toEntity(dto)
        assertEquals(0, user.id)
    }

    @Test
    fun userDTO_forUpdate_companionCreatesCorrectDto() {
        val dto = UserDTO.forUpdate(id = 5, nickname = "NewNick", image = "https://img.com/x")
        assertEquals(5, dto.idUser)
        assertEquals("NewNick", dto.nickname)
        assertEquals("https://img.com/x", dto.image)
    }
}
