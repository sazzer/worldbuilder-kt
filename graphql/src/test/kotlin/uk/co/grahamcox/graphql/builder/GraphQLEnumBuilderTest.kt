package uk.co.grahamcox.graphql.builder

import org.junit.Assert
import org.junit.Test

/**
 * Unit tests for the GraphQL Enum Builder
 */
class GraphQLEnumBuilderTest {
    enum class TestEnum {
        RED,
        GREEN,
        BLUE
    }

    /**
     * Test adding from an enum
     */
    @Test
    fun testAddFromEnumClass() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLEnumBuilder()
        builder.withMembers(TestEnum::class)

        Assert.assertEquals(mapOf("RED" to TestEnum.RED, "GREEN" to TestEnum.GREEN, "BLUE" to TestEnum.BLUE), builder.members)
    }

    /**
     * Test adding with individual calls
     */
    @Test
    fun testAddIndividually() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLEnumBuilder()
                .withMember("RED")
                .withMember("GREEN")
                .withMember("BLUE")

        Assert.assertEquals(mapOf("RED" to "RED", "GREEN" to "GREEN", "BLUE" to "BLUE"), builder.members)
    }

    /**
     * Test adding from a varargs call
     */
    @Test
    fun testAddVarargs() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLEnumBuilder()
                .withMembers("RED", "GREEN", "BLUE")

        Assert.assertEquals(mapOf("RED" to "RED", "GREEN" to "GREEN", "BLUE" to "BLUE"), builder.members)
    }

    /**
     * Test adding from a collection
     */
    @Test
    fun testAddCollection() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLEnumBuilder()
                .withMembers(setOf("RED", "GREEN", "BLUE"))

        Assert.assertEquals(mapOf("RED" to "RED", "GREEN" to "GREEN", "BLUE" to "BLUE"), builder.members)
    }

    /**
     * Test setting the description first
     */
    @Test
    fun testDescription() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLEnumBuilder()
                .withDescription("Hello")
                .withMembers(setOf("RED", "GREEN", "BLUE"))

        Assert.assertEquals("Hello", builder.description)
        Assert.assertEquals(mapOf("RED" to "RED", "GREEN" to "GREEN", "BLUE" to "BLUE"), builder.members)
    }
}