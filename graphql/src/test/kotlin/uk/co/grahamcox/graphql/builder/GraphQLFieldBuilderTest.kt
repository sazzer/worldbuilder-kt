package uk.co.grahamcox.graphql.builder

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.easymock.EasyMock
import org.junit.Assert
import org.junit.Test

/**
 * Unit Tests for the Field Builder
 */
class GraphQLFieldBuilderTest {
    /**
     * Test building a field with a static value
     */
    @Test
    fun testStaticValue() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLFieldBuilder()
                .withDescription("The Desc")
                .withType("string")
                .withDeprecationReason("It's old")
                .withValue("Hello")

        Assert.assertEquals("The Desc", builder.description)
        Assert.assertEquals(uk.co.grahamcox.graphql.builder.GraphQLTypeName("string", false, false, false), builder.type)
        Assert.assertEquals("It's old", builder.deprecationReason)
        Assert.assertNotNull(builder.fetcher)
        Assert.assertEquals("Hello", builder.fetcher?.get(null))
        Assert.assertTrue(builder.arguments.isEmpty())
    }

    /**
     * Test building a field with a dynamic fetcher
     */
    @Test
    fun testDynamicValue() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLFieldBuilder()
                .withFetcher(object : DataFetcher {
                    override fun get(environment: DataFetchingEnvironment?) = environment
                })

        val dfe = EasyMock.createMock(DataFetchingEnvironment::class.java)

        Assert.assertEquals(dfe, builder.fetcher?.get(dfe))
    }

    /**
     * Test building a field with arguments
     */
    @Test
    fun testArguments() {
        val builder = uk.co.grahamcox.graphql.builder.GraphQLFieldBuilder()
                .apply {
                    withArgument("name")
                            .withDescription("The name")
                    withArgument("age")
                            .withDescription("The age")
                }

        Assert.assertEquals(2, builder.arguments.size)
        Assert.assertNotNull(builder.arguments["name"])
        Assert.assertEquals("The name", builder.arguments["name"]?.description)
        Assert.assertNotNull(builder.arguments["age"])
        Assert.assertEquals("The age", builder.arguments["age"]?.description)
    }
}