package uk.co.grahamcox.dao.embedded.mongodb

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Unit Tests for the Seed Data Loader
 */
class SeedDataLoaderTest {
    @org.junit.Test
    fun testTransform() {
        val seedFile = "uk/co/grahamcox/dao/embedded/mongodb/transform.json"
        val loaded = SeedDataLoader.loadSeedData(seedFile)

        org.junit.Assert.assertNotNull(loaded)
        org.junit.Assert.assertEquals(1, loaded.size)
        org.junit.Assert.assertEquals(2, loaded[0].size)
        org.junit.Assert.assertEquals("abcde", loaded[0]["id"])
        org.junit.Assert.assertTrue(loaded[0]["time"] is Date)
    }
}