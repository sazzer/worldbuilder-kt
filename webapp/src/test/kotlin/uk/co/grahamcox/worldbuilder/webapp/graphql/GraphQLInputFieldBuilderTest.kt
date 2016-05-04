package uk.co.grahamcox.worldbuilder.webapp.graphql

import org.junit.Assert
import org.junit.Test

/**
 * Unit Tests for the Input Field Builder
 */
class GraphQLInputFieldBuilderTest {
    /**
     * Test setting all of the values
     */
    @Test
    fun testFull() {
        val builder = GraphQLInputFieldBuilder()
                .withDescription("The description")
                .withType("string")
                .withDefault("Hello")

        Assert.assertEquals("The description", builder.description)
        Assert.assertEquals(GraphQLTypeName("string", false, false, false), builder.type)
        Assert.assertEquals("Hello", builder.defaultValue)
    }
}