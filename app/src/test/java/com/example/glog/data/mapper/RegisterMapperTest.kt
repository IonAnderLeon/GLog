package com.example.glog.data.mapper

import com.example.glog.data.network.dto.RegisterDTO
import com.example.glog.data.network.dto.RegisterDetailDTO
import com.example.glog.domain.model.Register
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RegisterMapperTest {

    private lateinit var mapper: RegisterMapper

    @Before
    fun setUp() {
        mapper = RegisterMapper()
    }

    @Test
    fun toEntity_fromRegisterDetailDTO_mapsCorrectly() {
        val registerDto = RegisterDTO(
            idRegister = 100,
            date = "2024-01-15",
            idGame = 5,
            idUsuario = 1,
            playtime = 2.5
        )
        val detailDto = RegisterDetailDTO(
            register = registerDto,
            gameName = "Zelda",
            gameImageUrl = "https://img.com/z.jpg",
            userName = "Player1"
        )
        val entity = mapper.toEntity(detailDto)

        assertEquals(100, entity.id)
        assertEquals("2024-01-15", entity.date)
        assertEquals(2.5, entity.playtime)
        assertEquals(5, entity.gameId)
        assertEquals("Zelda", entity.gameName)
        assertEquals("https://img.com/z.jpg", entity.gameImageUrl)
        assertEquals(1, entity.userId)
        assertEquals("Player1", entity.userName)
    }

    @Test
    fun toEntity_nullRegister_usesDefaults() {
        val detailDto = RegisterDetailDTO(
            register = null,
            gameName = null,
            gameImageUrl = null,
            userName = null
        )
        val entity = mapper.toEntity(detailDto)

        assertEquals(0, entity.id)
        assertEquals("", entity.date)
        assertEquals(0.0, entity.playtime)
        assertEquals(0, entity.gameId)
        assertEquals("", entity.gameName)
        assertNull(entity.gameImageUrl)
        assertEquals(0, entity.userId)
        assertEquals("", entity.userName)
    }

    @Test
    fun toEntity_blankGameImageUrl_mapsToNull() {
        val registerDto = RegisterDTO(idRegister = 1, date = "2024-01-01", idGame = 1, idUsuario = 1)
        val detailDto = RegisterDetailDTO(
            register = registerDto,
            gameName = "Game",
            gameImageUrl = "",
            userName = "User"
        )
        val entity = mapper.toEntity(detailDto)
        assertNull(entity.gameImageUrl)
    }

    @Test
    fun toDto_mapsRegister_toRegisterDTO() {
        val register = Register(
            id = 42,
            date = "2024-02-01",
            playtime = 3.0,
            gameId = 10,
            gameName = "Elden Ring",
            gameImageUrl = "https://x.com/img.jpg",
            userId = 2,
            userName = "Gamer"
        )
        val dto = mapper.toDto(register)

        assertEquals(42, dto.idRegister)
        assertEquals("2024-02-01", dto.date)
        assertEquals(10, dto.idGame)
        assertEquals(2, dto.idUsuario)
        assertEquals(3.0, dto.playtime)
    }
}
