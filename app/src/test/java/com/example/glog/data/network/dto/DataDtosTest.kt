package com.example.glog.data.network.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests de DTOs para cobertura de líneas: constructores por defecto, copy, equals, todos los campos.
 */
class DataDtosTest {

    // --- AddGameToCollectionDTO ---
    @Test
    fun addGameToCollectionDTO_createsWithId() {
        val dto = AddGameToCollectionDTO(idGame = 42)
        assertEquals(42, dto.idGame)
    }

    @Test
    fun addGameToCollectionDTO_copy() {
        val dto = AddGameToCollectionDTO(idGame = 1)
        val copied = dto.copy(idGame = 2)
        assertEquals(2, copied.idGame)
    }

    @Test
    fun addGameToCollectionDTO_equals_hashCode() {
        val a = AddGameToCollectionDTO(idGame = 1)
        val b = AddGameToCollectionDTO(idGame = 1)
        assertTrue(a == b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    // --- AppPreferences (data/preferences) ---
    @Test
    fun appPreferences_defaults() {
        val prefs = com.example.glog.data.preferences.AppPreferences()
        assertNull(prefs.useDarkTheme)
        assertEquals(false, prefs.useLargeText)
    }

    @Test
    fun appPreferences_withValues() {
        val prefs = com.example.glog.data.preferences.AppPreferences(
            useDarkTheme = true,
            useLargeText = true
        )
        assertEquals(true, prefs.useDarkTheme)
        assertEquals(true, prefs.useLargeText)
    }

    @Test
    fun appPreferences_copy() {
        val prefs = com.example.glog.data.preferences.AppPreferences(useDarkTheme = false)
        val copied = prefs.copy(useLargeText = true)
        assertEquals(false, copied.useDarkTheme)
        assertEquals(true, copied.useLargeText)
    }

    @Test
    fun appPreferences_toString() {
        val prefs = com.example.glog.data.preferences.AppPreferences(useDarkTheme = true)
        assertTrue(prefs.toString().contains("useDarkTheme=true"))
    }

    // --- CollectionDTO ---
    @Test
    fun collectionDTO_defaults() {
        val dto = CollectionDTO()
        assertNull(dto.description)
        assertNull(dto.idCollection)
        assertNull(dto.name)
    }

    @Test
    fun collectionDTO_defaultsAndValues() {
        val dto = CollectionDTO(
            idCollection = 1,
            name = "Fav",
            description = "Desc"
        )
        assertEquals(1, dto.idCollection)
        assertEquals("Fav", dto.name)
        assertEquals("Desc", dto.description)
    }

    @Test
    fun collectionDTO_copy() {
        val dto = CollectionDTO(idCollection = 1, name = "A", description = "D")
        val copied = dto.copy(name = "B")
        assertEquals(1, copied.idCollection)
        assertEquals("B", copied.name)
    }

    @Test
    fun collectionGamesDTO_defaults() {
        val dto = CollectionGamesDTO()
        assertNull(dto.collection)
        assertNull(dto.games)
    }

    @Test
    fun collectionGamesDTO_createsWithCollectionAndGames() {
        val col = CollectionDTO(idCollection = 1, name = "C", description = null)
        val dto = CollectionGamesDTO(collection = col, games = emptyList())
        assertEquals(1, dto.collection?.idCollection)
        assertEquals(0, dto.games?.size ?: 0)
    }

    @Test
    fun collectionGamesDTO_withNullsInGamesList() {
        val game = GameDTO(id = 1, name = "G")
        val dto = CollectionGamesDTO(
            collection = CollectionDTO(idCollection = 1, name = "Col", description = null),
            games = listOf(null, game, null)
        )
        assertEquals(3, dto.games?.size)
        assertNull(dto.games?.get(0))
        assertEquals(1, dto.games?.get(1)?.id)
    }

    @Test
    fun collectionGamesDTO_copy() {
        val dto = CollectionGamesDTO(collection = CollectionDTO(idCollection = 1, name = "X", description = null))
        val copied = dto.copy(games = listOf(GameDTO(id = 1)))
        assertEquals(1, copied.games?.size)
    }

    // --- GameDTO ---
    @Test
    fun gameDTO_defaults() {
        val dto = GameDTO()
        assertNull(dto.genreId)
        assertNull(dto.id)
        assertNull(dto.image)
        assertNull(dto.name)
        assertNull(dto.platformId)
        assertNull(dto.rating)
        assertNull(dto.year)
        assertNull(dto.description)
    }

    @Test
    fun gameDTO_allFields() {
        val dto = GameDTO(
            id = 1,
            name = "G",
            image = "url",
            year = 2023,
            rating = 4.0,
            genreId = 1,
            platformId = 1,
            description = "D"
        )
        assertEquals(1, dto.id)
        assertEquals("G", dto.name)
        assertEquals("url", dto.image)
        assertEquals(2023, dto.year)
        assertEquals(4.0, dto.rating)
        assertEquals(1, dto.genreId)
        assertEquals(1, dto.platformId)
        assertEquals("D", dto.description)
    }

    @Test
    fun gameDTO_partialFields() {
        val dto = GameDTO(id = 10, name = "Only")
        assertEquals(10, dto.id)
        assertEquals("Only", dto.name)
        assertNull(dto.image)
        assertNull(dto.year)
    }

    @Test
    fun gameDTO_copy() {
        val dto = GameDTO(id = 1, name = "A", rating = 3.0)
        val copied = dto.copy(name = "B", year = 2024)
        assertEquals(1, copied.id)
        assertEquals("B", copied.name)
        assertEquals(2024, copied.year)
    }

    @Test
    fun gameDTO_equals() {
        val a = GameDTO(id = 1, name = "G")
        val b = GameDTO(id = 1, name = "G")
        assertTrue(a == b)
    }

    // --- GameDetailDTO ---
    @Test
    fun gameDetailDTO_defaults() {
        val dto = GameDetailDTO()
        assertNull(dto.game)
        assertNull(dto.genreName)
        assertNull(dto.platformName)
    }

    @Test
    fun gameDetailDTO_createsWithGameAndNames() {
        val game = GameDTO(id = 1, name = "X")
        val dto = GameDetailDTO(game = game, genreName = "RPG", platformName = "PC")
        assertEquals(1, dto.game?.id)
        assertEquals("RPG", dto.genreName)
        assertEquals("PC", dto.platformName)
    }

    @Test
    fun gameDetailDTO_copy() {
        val dto = GameDetailDTO(genreName = "RPG", platformName = "PC")
        val copied = dto.copy(platformName = "Switch")
        assertEquals("Switch", copied.platformName)
    }

    // --- RegisterDTO ---
    @Test
    fun registerDTO_defaults() {
        val dto = RegisterDTO()
        assertNull(dto.date)
        assertNull(dto.idGame)
        assertNull(dto.idRegister)
        assertNull(dto.idUsuario)
        assertNull(dto.playtime)
    }

    @Test
    fun registerDTO_allFields() {
        val dto = RegisterDTO(
            idRegister = 1,
            date = "2024-01-01",
            idGame = 10,
            idUsuario = 2,
            playtime = 1.5
        )
        assertEquals(1, dto.idRegister)
        assertEquals("2024-01-01", dto.date)
        assertEquals(10, dto.idGame)
        assertEquals(2, dto.idUsuario)
        assertEquals(1.5, dto.playtime)
    }

    @Test
    fun registerDTO_copy() {
        val dto = RegisterDTO(idRegister = 1, date = "2024-01-01")
        val copied = dto.copy(playtime = 2.0)
        assertEquals(2.0, copied.playtime)
    }

    // --- RegisterDetailDTO ---
    @Test
    fun registerDetailDTO_defaults() {
        val dto = RegisterDetailDTO()
        assertNull(dto.gameName)
        assertNull(dto.gameImageUrl)
        assertNull(dto.register)
        assertNull(dto.userName)
    }

    @Test
    fun registerDetailDTO_createsWithAllFields() {
        val reg = RegisterDTO(idRegister = 1, date = "2024-01-01", idGame = 1, idUsuario = 1)
        val dto = RegisterDetailDTO(
            register = reg,
            gameName = "Zelda",
            gameImageUrl = "url",
            userName = "User"
        )
        assertEquals(1, dto.register?.idRegister)
        assertEquals("Zelda", dto.gameName)
        assertEquals("url", dto.gameImageUrl)
        assertEquals("User", dto.userName)
    }

    @Test
    fun registerDetailDTO_copy() {
        val dto = RegisterDetailDTO(gameName = "G", userName = "U")
        val copied = dto.copy(gameImageUrl = "img")
        assertEquals("img", copied.gameImageUrl)
    }

    // --- UserDTO ---
    @Test
    fun userDTO_defaults() {
        val dto = UserDTO()
        assertNull(dto.idUser)
        assertNull(dto.image)
        assertNull(dto.nickname)
    }

    @Test
    fun userDTO_withValues() {
        val dto = UserDTO(idUser = 1, nickname = "Gamer", image = "avatar.png")
        assertEquals(1, dto.idUser)
        assertEquals("Gamer", dto.nickname)
        assertEquals("avatar.png", dto.image)
    }

    @Test
    fun userDTO_forUpdate_companion() {
        val dto = UserDTO.forUpdate(5, "Nick", "img.png")
        assertEquals(5, dto.idUser)
        assertEquals("Nick", dto.nickname)
        assertEquals("img.png", dto.image)
    }

    @Test
    fun userDTO_forUpdate_withNulls() {
        val dto = UserDTO.forUpdate(1, null, null)
        assertEquals(1, dto.idUser)
        assertNull(dto.nickname)
        assertNull(dto.image)
    }

    @Test
    fun userDTO_copy() {
        val dto = UserDTO(idUser = 1, nickname = "A")
        val copied = dto.copy(nickname = "B", image = "x")
        assertEquals("B", copied.nickname)
        assertEquals("x", copied.image)
    }

    @Test
    fun userDTO_equals() {
        val a = UserDTO(idUser = 1, nickname = "N")
        val b = UserDTO(idUser = 1, nickname = "N")
        assertTrue(a == b)
        val c = UserDTO(idUser = 2, nickname = "N")
        assertFalse(a == c)
    }
}
