package uk.co.grahamcox.graphql.builder

import org.junit.Assert
import org.junit.Test

/**
 * Unit Tests for the GraphQL Union Builder
 */
class GraphQLUnionBuilderTest {
    /**
     * Test registering a custom type resolver
     */
    @Test
    fun testCustomResolver() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLUnionBuilder()
                .withDescription("The description")
                .withType("first", { o -> "first" == o })
                .withType("second", { o -> 2 == o })

        Assert.assertEquals("The description", builder.description)
        Assert.assertEquals(2, builder.unionTypes.size)

        Assert.assertTrue(builder.unionTypes.containsKey("first"))
        Assert.assertTrue(builder.unionTypes["first"]!!.invoke("first"))

        Assert.assertTrue(builder.unionTypes.containsKey("second"))
        Assert.assertTrue(builder.unionTypes["second"]!!.invoke(2))
    }

    /**
     * Test registering a type resolver against class types
     */
    @Test
    fun testClassResolver() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLUnionBuilder()
                .withDescription("The description")
                .withType("first", String::class)
                .withType("second", Number::class)

        Assert.assertEquals("The description", builder.description)
        Assert.assertEquals(2, builder.unionTypes.size)

        Assert.assertTrue(builder.unionTypes.containsKey("first"))
        Assert.assertTrue(builder.unionTypes["first"]!!.invoke("first"))

        Assert.assertTrue(builder.unionTypes.containsKey("second"))
        Assert.assertTrue(builder.unionTypes["second"]!!.invoke(2))
    }
}