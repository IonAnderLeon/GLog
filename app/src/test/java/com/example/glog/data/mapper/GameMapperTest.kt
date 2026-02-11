package com.example.glog.data.mapper

import com.example.glog.data.network.dto.GameDTO
import com.example.glog.data.network.dto.GameDetailDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GameMapperTest {

    private lateinit var mapper: GameMapper

    @Before
    fun setUp() {
        mapper = GameMapper()
    }

    @Test
    fun toEntity_fromGameDTO_withFullData_mapsCorrectly() {
        val dto = GameDTO(
            id = 10,
            name = "Zelda",
            image = "https://example.com/zelda.jpg",
            year = 2023,
            rating = 4.5,
            genreId = 2,
            platformId = 4,
            description = "A great game"
        )
        val entity = mapper.toEntity(dto)

        assertEquals(10, entity.id)
        assertEquals("Zelda", entity.title)
        assertEquals("https://example.com/zelda.jpg", entity.imageUrl)
        assertEquals(2023, entity.releaseYear)
        assertEquals(4.5, entity.rating)
        assertEquals("Aventura", entity.genreName)
        assertEquals("Switch", entity.platformName)
        assertEquals("A great game", entity.description)
    }

    @Test
    fun toEntity_fromGameDTO_withNulls_usesDefaults() {
        val dto = GameDTO(
            id = null,
            name = null,
            image = null,
            year = null,
            rating = null,
            genreId = null,
            platformId = null,
            description = null
        )
        val entity = mapper.toEntity(dto)

        assertEquals(0, entity.id)
        assertEquals("Sin título", entity.title)
        assertNull(entity.imageUrl)
        assertNull(entity.releaseYear)
        assertEquals(0.0, entity.rating)
        assertEquals("Género desconocido", entity.genreName)
        assertEquals("Plataforma desconocida", entity.platformName)
        assertNull(entity.description)
    }

    @Test
    fun toEntity_fromGameDTO_blankImage_mapsToNull() {
        val dto = GameDTO(id = 1, name = "Game", image = "", description = " ")
        val entity = mapper.toEntity(dto)
        assertNull(entity.imageUrl)
        assertNull(entity.description)
    }

    @Test
    fun toEntity_fromGameDetailDTO_withFullGame_mapsCorrectly() {
        val gameDto = GameDTO(
            id = 5,
            name = "Elden Ring",
            image = "https://img.com/elden.jpg",
            year = 2022,
            rating = 4.8,
            genreId = 3,
            platformId = 1,
            description = "Souls-like"
        )
        val detailDto = GameDetailDTO(
            game = gameDto,
            genreName = "RPG",
            platformName = "PC"
        )
        val entity = mapper.toEntity(detailDto)

        assertEquals(5, entity.id)
        assertEquals("Elden Ring", entity.title)
        assertEquals("https://img.com/elden.jpg", entity.imageUrl)
        assertEquals(2022, entity.releaseYear)
        assertEquals(4.8, entity.rating)
        assertEquals("RPG", entity.genreName)
        assertEquals("PC", entity.platformName)
        assertEquals("Souls-like", entity.description)
    }

    @Test
    fun toEntity_fromGameDetailDTO_nullGame_usesDefaults() {
        val detailDto = GameDetailDTO(game = null, genreName = null, platformName = null)
        val entity = mapper.toEntity(detailDto)

        assertEquals(0, entity.id)
        assertEquals("Sin título", entity.title)
        assertNull(entity.imageUrl)
        assertNull(entity.releaseYear)
        assertEquals(0.0, entity.rating)
        assertEquals("Plataforma desconocida", entity.platformName)
        assertEquals("Género desconocido", entity.genreName)
    }
}
