package com.example.glog.data.mapper.constants

import org.junit.Assert.assertEquals
import org.junit.Test

class GenreConstantsTest {

    @Test
    fun getGenreNameById_returnsCorrectName_forKnownIds() {
        assertEquals("Acción", GenreConstants.getGenreNameById(1))
        assertEquals("Aventura", GenreConstants.getGenreNameById(2))
        assertEquals("RPG", GenreConstants.getGenreNameById(3))
        assertEquals("Estrategia", GenreConstants.getGenreNameById(4))
        assertEquals("Deportes", GenreConstants.getGenreNameById(5))
        assertEquals("Carreras", GenreConstants.getGenreNameById(6))
        assertEquals("Shooter", GenreConstants.getGenreNameById(7))
        assertEquals("Indie", GenreConstants.getGenreNameById(8))
        assertEquals("Simulación", GenreConstants.getGenreNameById(9))
        assertEquals("Puzzle", GenreConstants.getGenreNameById(10))
        assertEquals("Skibidi", GenreConstants.getGenreNameById(11))
    }

    @Test
    fun getGenreNameById_returnsDesconocido_forUnknownId() {
        assertEquals("Género desconocido", GenreConstants.getGenreNameById(0))
        assertEquals("Género desconocido", GenreConstants.getGenreNameById(99))
        assertEquals("Género desconocido", GenreConstants.getGenreNameById(-1))
    }
}
