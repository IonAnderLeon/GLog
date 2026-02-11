package com.example.glog.data.mapper.constants

import org.junit.Assert.assertEquals
import org.junit.Test

class PlatformConstantsTest {

    @Test
    fun getPlatformNameById_returnsCorrectName_forKnownIds() {
        assertEquals("PC", PlatformConstants.getPlatformNameById(1))
        assertEquals("PS5", PlatformConstants.getPlatformNameById(2))
        assertEquals("XboxX", PlatformConstants.getPlatformNameById(3))
        assertEquals("Switch", PlatformConstants.getPlatformNameById(4))
        assertEquals("PS4", PlatformConstants.getPlatformNameById(5))
        assertEquals("XboxOne", PlatformConstants.getPlatformNameById(6))
        assertEquals("Android", PlatformConstants.getPlatformNameById(7))
        assertEquals("iOS", PlatformConstants.getPlatformNameById(8))
        assertEquals("Wii", PlatformConstants.getPlatformNameById(17))
    }

    @Test
    fun getPlatformNameById_returnsDesconocida_forUnknownId() {
        assertEquals("Plataforma desconocida", PlatformConstants.getPlatformNameById(0))
        assertEquals("Plataforma desconocida", PlatformConstants.getPlatformNameById(99))
        assertEquals("Plataforma desconocida", PlatformConstants.getPlatformNameById(-1))
    }
}
