package com.example.glog.data.network.routes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KTest {

    @Test
    fun baseUrl_endsWithApi() {
        assertTrue(K.BASE_URL.endsWith("api/"))
    }

    @Test
    fun querySearch_hasExpectedValue() {
        assertEquals("busqueda", K.QUERY_SEARCH)
    }

    @Test
    fun gamesRoutes_areCorrect() {
        assertEquals("games", K.GAMES)
        assertEquals("games/{id}", K.GAME_BY_ID)
    }

    @Test
    fun usersRoutes_areCorrect() {
        assertEquals("users", K.USERS)
        assertEquals("users/{id}", K.USER_BY_ID)
    }

    @Test
    fun registersRoutes_areCorrect() {
        assertEquals("registers", K.REGISTERS)
        assertEquals("registers/{id}", K.REGISTERS_BY_ID)
        assertEquals("registers/user/{userId}", K.REGISTERS_BY_USER)
    }

    @Test
    fun collectionsRoutes_areCorrect() {
        assertEquals("collections", K.COLLECTIONS)
        assertEquals("collections/{id}", K.COLLECTION_BY_ID)
        assertEquals("collections/{id}/games", K.COLLECTION_ADD_GAME)
        assertEquals("collections/{collectionId}/games/{gameId}", K.COLLECTION_REMOVE_GAME)
    }
}
