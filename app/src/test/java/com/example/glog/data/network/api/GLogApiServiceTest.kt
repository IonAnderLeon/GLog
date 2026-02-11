package com.example.glog.data.network.api

import com.example.glog.data.network.dto.AddGameToCollectionDTO
import com.example.glog.data.network.dto.CollectionDTO
import com.example.glog.data.network.dto.GameDTO
import com.example.glog.data.network.dto.GameDetailDTO
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GLogApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: GLogApiService
    private val gson = Gson()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create()) // 👈 GSON CONVERTER
            .build()

        apiService = retrofit.create(GLogApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getAllGames_shouldReturnGames() = runTest {
        // GIVEN - Creamos objeto y lo convertimos a JSON con Gson
        val gameDetail = GameDetailDTO(
            game = GameDTO(id = 1, name = "Zelda", genreId = 2, platformId = 4),
            genreName = "Aventura",
            platformName = "Switch"
        )

        val json = gson.toJson(listOf(gameDetail))

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        // WHEN
        val response = apiService.getAllGames()

        // THEN
        assertEquals(1, response.size)
        assertEquals("Zelda", response[0].game?.name)

        val request = mockWebServer.takeRequest()
        assertEquals("/games", request.path)
    }

    @Test
    fun getGameById_shouldReturnGameDetail() = runTest {
        // GIVEN
        val gameDetail = GameDetailDTO(
            game = GameDTO(id = 5, name = "Elden Ring", genreId = 3, platformId = 1),
            genreName = "RPG",
            platformName = "PC"
        )

        val json = gson.toJson(gameDetail)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        // WHEN
        val response = apiService.getGameById(5)

        // THEN
        assertEquals(5, response.game?.id)
        assertEquals("Elden Ring", response.game?.name)

        val request = mockWebServer.takeRequest()
        assertEquals("/games/5", request.path)
    }

    @Test
    fun getUsers_withSearchQuery_shouldSendQueryParam() = runTest {
        // GIVEN
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        // WHEN
        apiService.getUsers("gamer")

        // THEN
        val request = mockWebServer.takeRequest()
        assertEquals("/users?busqueda=gamer", request.path)
    }

    @Test
    fun createCollection_shouldSendPostRequest() = runTest {
        // GIVEN
        val collectionDto = CollectionDTO("New", 7, "Desc")
        val json = gson.toJson(collectionDto)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        // WHEN
        val dto = CollectionDTO( "New", 0, "Desc")
        val response = apiService.createCollection(dto)

        // THEN
        assertNotNull(response.body())
        assertEquals(7, response.body()?.idCollection)

        val request = mockWebServer.takeRequest()
        assertEquals("/collections", request.path)
        assertEquals("POST", request.method)
    }

    @Test
    fun addGameToCollection_shouldSendCorrectBody() = runTest {
        // GIVEN
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
        )

        // WHEN
        val body = AddGameToCollectionDTO(42)
        apiService.addGameToCollection(1L, body)

        // THEN
        val request = mockWebServer.takeRequest()
        assertEquals("/collections/1/games", request.path)
        assertEquals("POST", request.method)

        // Verificar body con Gson
        val bodyJson = request.body.readUtf8()
        val bodyMap = gson.fromJson(bodyJson, Map::class.java)
        assertEquals(42.0, bodyMap["idGame"]) // Gson lee números como Double
    }
}