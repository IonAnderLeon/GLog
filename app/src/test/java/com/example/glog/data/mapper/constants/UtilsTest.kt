package com.example.glog.data.mapper.constants

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests para las funciones internal de [Utils].
 * Mismo módulo = internal visible desde test.
 */
class UtilsTest {

    @Test
    fun formatEmpty_null_returnsDefault() {
        with(Utils) {
            assertEquals("Desconocido", (null as String?).formatEmpty())
            assertEquals("Custom", (null as String?).formatEmpty("Custom"))
        }
    }

    @Test
    fun formatEmpty_blank_returnsDefault() {
        with(Utils) {
            assertEquals("Desconocido", "".formatEmpty())
            assertEquals("x", "   ".formatEmpty("x"))
        }
    }

    @Test
    fun formatEmpty_nonBlank_returnsValue() {
        with(Utils) {
            assertEquals("Zelda", "Zelda".formatEmpty())
            assertEquals("PC", "PC".formatEmpty("Desconocido"))
        }
    }

    @Test
    fun formatGenre_null_returnsDesconocido() {
        with(Utils) {
            assertEquals("Género desconocido", (null as Int?).formatGenre())
        }
    }

    @Test
    fun formatGenre_knownId_returnsName() {
        with(Utils) {
            assertEquals("Acción", 1.formatGenre())
            assertEquals("Aventura", 2.formatGenre())
            assertEquals("RPG", 3.formatGenre())
        }
    }

    @Test
    fun formatGenre_unknownId_returnsDesconocido() {
        with(Utils) {
            assertEquals("Género desconocido", 999.formatGenre())
        }
    }

    @Test
    fun formatPlatform_null_returnsDesconocida() {
        with(Utils) {
            assertEquals("Plataforma desconocida", (null as Int?).formatPlatform())
        }
    }

    @Test
    fun formatPlatform_knownId_returnsName() {
        with(Utils) {
            assertEquals("PC", 1.formatPlatform())
            assertEquals("Switch", 4.formatPlatform())
        }
    }

    @Test
    fun formatPlatform_unknownId_returnsDesconocida() {
        with(Utils) {
            assertEquals("Plataforma desconocida", 99.formatPlatform())
        }
    }

    @Test
    fun formatGenres_null_returnsDesconocidos() {
        with(Utils) {
            assertEquals("Géneros desconocidos", (null as Int?).formatGenres())
        }
    }

    @Test
    fun formatGenres_nonNull_delegatesToFormatGenre() {
        with(Utils) {
            assertEquals("Acción", 1.formatGenres())
        }
    }

    @Test
    fun formatPlatforms_null_returnsDesconocidas() {
        with(Utils) {
            assertEquals("Plataformas desconocidas", (null as Int?).formatPlatforms())
        }
    }

    @Test
    fun formatPlatforms_nonNull_delegatesToFormatPlatform() {
        with(Utils) {
            assertEquals("PC", 1.formatPlatforms())
        }
    }
}
