package com.example.glog.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DestinationTest {

    @Test
    fun home_hasCorrectRoute() {
        assertEquals("home", Destination.Home.route)
        assertTrue(Destination.Home.hasBottomBar)
    }

    @Test
    fun cluster_hasCorrectRoute() {
        assertEquals("cluster", Destination.Cluster.route)
    }

    @Test
    fun profile_hasCorrectRoute() {
        assertEquals("profile", Destination.Profile.route)
    }

    @Test
    fun gameDetails_createRoute_buildsCorrectRoute() {
        assertEquals("gameInfo/42", Destination.GameDetails.createRoute("42"))
        assertEquals("gameInfo/1", Destination.GameDetails.createRoute("1"))
    }

    @Test
    fun gameDetails_routePattern_containsId() {
        assertTrue(Destination.GameDetails.route.contains("{id}"))
    }

    @Test
    fun bottomNavItems_containsThreeItems() {
        assertEquals(3, bottomNavItems.size)
    }

    @Test
    fun bottomNavItems_hasCorrectDestinations() {
        assertEquals(Destination.Home, bottomNavItems[0].destination)
        assertEquals(Destination.Cluster, bottomNavItems[1].destination)
        assertEquals(Destination.Profile, bottomNavItems[2].destination)
    }
}
