package uk.co.grahamcox.worldbuilder.dao.embedded

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Unit Tests for the Seed Data Loader
 */
class SeedDataLoaderTest {
    @Test
    fun testTransform() {
        val seedFile = "uk/co/grahamcox/worldbuilder/dao/embedded/transform.json"
        val loaded = SeedDataLoader.loadSeedData(seedFile)

        Assert.assertNotNull(loaded)
        Assert.assertEquals(1, loaded.size)
        Assert.assertEquals(2, loaded[0].size)
        Assert.assertEquals("abcde", loaded[0]["id"])
        Assert.assertTrue(loaded[0]["time"] is Date)
    }
}