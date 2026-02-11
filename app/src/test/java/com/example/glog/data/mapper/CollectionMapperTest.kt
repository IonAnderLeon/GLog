package com.example.glog.data.mapper

import com.example.glog.data.network.dto.CollectionDTO
import com.example.glog.data.network.dto.CollectionGamesDTO
import com.example.glog.data.network.dto.GameDTO
import com.example.glog.domain.model.Collection
import com.example.glog.domain.model.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CollectionMapperTest {

    private lateinit var gameMapper: GameMapper
    private lateinit var collectionMapper: CollectionMapper

    @Before
    fun setUp() {
        gameMapper = GameMapper()
        collectionMapper = CollectionMapper(gameMapper)
    }

    @Test
    fun toEntity_mapsCollectionGamesDTO_toCollection() {
        val gameDto = GameDTO(id = 1, name = "Game A", genreId = 1, platformId = 1)
        val collectionDto = CollectionDTO(
            idCollection = 10,
            name = "Favoritos",
            description = "Mis favoritos"
        )
        val dto = CollectionGamesDTO(
            collection = collectionDto,
            games = listOf(gameDto)
        )

        val entity = collectionMapper.toEntity(dto)

        assertEquals(10, entity.id)
        assertEquals("Favoritos", entity.name)
        assertEquals("Mis favoritos", entity.description)
        assertEquals(1, entity.games.size)
        assertEquals(1, entity.games[0].id)
        assertEquals("Game A", entity.games[0].title)
        assertEquals(listOf("1"), entity.gameIds)
    }

    @Test
    fun toEntity_nullCollection_usesDefaults() {
        val dto = CollectionGamesDTO(collection = null, games = null)
        val entity = collectionMapper.toEntity(dto)

        assertEquals(0, entity.id)
        assertEquals("", entity.name)
        assertNull(entity.description)
        assertEquals(emptyList<Game>(), entity.games)
        assertEquals(emptyList<String>(), entity.gameIds)
    }

    @Test
    fun toEntity_nullGames_returnsEmptyGameList() {
        val collectionDto = CollectionDTO(
            idCollection = 1,
            name = "Empty",
            description = null
        )
        val dto = CollectionGamesDTO(collection = collectionDto, games = null)
        val entity = collectionMapper.toEntity(dto)

        assertEquals(1, entity.id)
        assertEquals("Empty", entity.name)
        assertEquals(0, entity.games.size)
    }

    @Test
    fun toEntity_gamesListWithNulls_filtersNulls() {
        val gameDto = GameDTO(id = 2, name = "Only", genreId = 1, platformId = 1)
        val collectionDto = CollectionDTO(idCollection = 1, name = "Col", description = null)
        val dto = CollectionGamesDTO(
            collection = collectionDto,
            games = listOf(null, gameDto, null)
        )
        val entity = collectionMapper.toEntity(dto)
        assertEquals(1, entity.games.size)
        assertEquals(2, entity.games[0].id)
        assertEquals("Only", entity.games[0].title)
        assertEquals(listOf("2"), entity.gameIds)
    }

    @Test
    fun toDto_mapsCollection_toCollectionDTO() {
        val collection = Collection(
            id = 5,
            name = "Wishlist",
            description = "Por jugar"
        )
        val dto = collectionMapper.toDto(collection)

        assertEquals(5, dto.idCollection)
        assertEquals("Wishlist", dto.name)
        assertEquals("Por jugar", dto.description)
    }
}
