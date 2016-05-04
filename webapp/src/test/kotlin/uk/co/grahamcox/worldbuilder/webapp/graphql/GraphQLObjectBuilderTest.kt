package uk.co.grahamcox.worldbuilder.webapp.graphql

import org.junit.Assert
import org.junit.Test

/**
 * Unit Tests for the Object Builder
 */
class GraphQLObjectBuilderTest {
    /**
     * Test setting fields
     */
    @Test
    fun testFields() {
        val builder = GraphQLObjectBuilder()
                .withDescription("The Desc")
                .apply {
                    withField("name")
                            .withDescription("The Name")
                    withField("age")
                            .withDescription("The Age")
                }

        Assert.assertEquals("The Desc", builder.description)
        Assert.assertEquals(2, builder.fields.size)
        Assert.assertNotNull(builder.fields["name"])
        Assert.assertEquals("The Name", builder.fields["name"]?.description)
        Assert.assertNotNull(builder.fields["age"])
        Assert.assertEquals("The Age", builder.fields["age"]?.description)
    }

    /**
     * Test adding interfaces one at a time
     */
    @Test
    fun testSingleInterfaces() {
        val builder = GraphQLObjectBuilder()
                .withInterface("a")
                .withInterface("b")
                .withInterface("c")

        Assert.assertEquals(setOf("a", "b", "c"), builder.interfaces)
    }

    /**
     * Test adding interfaces as a varargs call
     */
    @Test
    fun testVarargsInterfaces() {
        val builder = GraphQLObjectBuilder()
                .withInterfaces("a", "b", "c")

        Assert.assertEquals(setOf("a", "b", "c"), builder.interfaces)
    }

    /**
     * Test adding interfaces as a collection call
     */
    @Test
    fun testInterfacesCollection() {
        val builder = GraphQLObjectBuilder()
                .withInterfaces(setOf("a", "b", "c"))

        Assert.assertEquals(setOf("a", "b", "c"), builder.interfaces)
    }
}