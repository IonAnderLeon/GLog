package com.example.glog.data.network.api

import com.example.glog.data.network.dto.AddGameToCollectionDTO
import com.example.glog.data.network.dto.CollectionDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GLogApiServiceRealTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: GLogApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        apiService = retrofit.create(GLogApiService::class.java) // 👈 INSTANCIA REAL
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getAllGames_shouldReturnGames() = runTest {
        // GIVEN - Mockeamos la respuesta HTTP
        val json = """
            [
                {
                    "game": {
                        "id": 1,
                        "name": "Zelda",
                        "genreId": 2,
                        "platformId": 4
                    },
                    "genreName": "Aventura",
                    "platformName": "Switch"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        // WHEN - Llamada REAL al ApiService
        val response = apiService.getAllGames()

        // THEN - Verificamos que el código REAL se ejecutó
        assertEquals(1, response.size)
        assertEquals("Zelda", response[0].game?.name)

        // Verificar que se llamó al endpoint correcto
        val request = mockWebServer.takeRequest()
        assertEquals("/games", request.path)
    }

    @Test
    fun getGameById_shouldReturnGameDetail() = runTest {
        // GIVEN
        val json = """
            {
                "game": {
                    "id": 5,
                    "name": "Elden Ring",
                    "genreId": 3,
                    "platformId": 1
                },
                "genreName": "RPG",
                "platformName": "PC"
            }
        """.trimIndent()

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

        // THEN - Verificamos que el query param se envió
        val request = mockWebServer.takeRequest()
        assertEquals("/users?busqueda=gamer", request.path)
    }

    @Test
    fun createCollection_shouldSendPostRequest() = runTest {
        // GIVEN
        val json = """
            {
                "idCollection": 7,
                "name": "New",
                "description": "Desc"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        // WHEN
        val dto = CollectionDTO("String", 1, "Desc")
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

        // Verificar el body
        val bodyJson = request.body.readUtf8()
        assertTrue(bodyJson.contains(""""idGame":42"""))
    }
}