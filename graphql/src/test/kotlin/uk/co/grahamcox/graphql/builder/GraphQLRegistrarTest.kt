package uk.co.grahamcox.graphql.builder

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit Tests for the GraphQL Registrar
 */
@RunWith(JUnitParamsRunner::class)
class GraphQLRegistrarTest {
    /**
     * Test adding a new object
     */
    @Test
    fun testNewObject() {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        val builder = registrar.newObject("obj")

        Assert.assertEquals(1, registrar.objectBuilders.size)
        Assert.assertEquals(builder, registrar.objectBuilders["obj"])
        Assert.assertTrue(registrar.enumBuilders.isEmpty())
        Assert.assertTrue(registrar.interfaceBuilders.isEmpty())
        Assert.assertTrue(registrar.queryBuilders.isEmpty())
        Assert.assertTrue(registrar.unionBuilders.isEmpty())
    }

    /**
     * Test adding a new interface
     */
    @Test
    fun testNewInterface() {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        val builder = registrar.newInterface("obj")

        Assert.assertEquals(1, registrar.interfaceBuilders.size)
        Assert.assertEquals(builder, registrar.interfaceBuilders["obj"])
        Assert.assertTrue(registrar.enumBuilders.isEmpty())
        Assert.assertTrue(registrar.objectBuilders.isEmpty())
        Assert.assertTrue(registrar.queryBuilders.isEmpty())
        Assert.assertTrue(registrar.unionBuilders.isEmpty())
    }

    /**
     * Test adding a new enum
     */
    @Test
    fun testNewEnum() {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        val builder = registrar.newEnum("obj")

        Assert.assertEquals(1, registrar.enumBuilders.size)
        Assert.assertEquals(builder, registrar.enumBuilders["obj"])
        Assert.assertTrue(registrar.objectBuilders.isEmpty())
        Assert.assertTrue(registrar.interfaceBuilders.isEmpty())
        Assert.assertTrue(registrar.queryBuilders.isEmpty())
        Assert.assertTrue(registrar.unionBuilders.isEmpty())
    }

    /**
     * Test adding a new query
     */
    @Test
    fun testNewQuery() {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        val builder = registrar.newQuery("obj")

        Assert.assertEquals(1, registrar.queryBuilders.size)
        Assert.assertEquals(builder, registrar.queryBuilders["obj"])
        Assert.assertTrue(registrar.enumBuilders.isEmpty())
        Assert.assertTrue(registrar.interfaceBuilders.isEmpty())
        Assert.assertTrue(registrar.objectBuilders.isEmpty())
        Assert.assertTrue(registrar.unionBuilders.isEmpty())
    }

    /**
     * Test adding a new union
     */
    @Test
    fun testNewUnion() {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        val builder = registrar.newUnion("obj")

        Assert.assertEquals(1, registrar.unionBuilders.size)
        Assert.assertEquals(builder, registrar.unionBuilders["obj"])
        Assert.assertTrue(registrar.enumBuilders.isEmpty())
        Assert.assertTrue(registrar.interfaceBuilders.isEmpty())
        Assert.assertTrue(registrar.objectBuilders.isEmpty())
        Assert.assertTrue(registrar.queryBuilders.isEmpty())
    }

    /**
     * Test that adding an object checks for duplicate names on all other types
     */
    @Test
    @Parameters("object", "interface", "enum", "union")
    fun testDuplicateNamesObject(name: String) {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")
        registrar.newUnion("union")

        try {
            registrar.newObject(name)
            Assert.fail("Expected an IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }

    /**
     * Test that adding an interface checks for duplicate names on all other types
     */
    @Test
    @Parameters("object", "interface", "enum", "union")
    fun testDuplicateNamesInterface(name: String) {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")
        registrar.newUnion("union")

        try {
            registrar.newInterface(name)
            Assert.fail("Expected an IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }

    /**
     * Test that adding an enum checks for duplicate names on all other types
     */
    @Test
    @Parameters("object", "interface", "enum", "union")
    fun testDuplicateNamesEnum(name: String) {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")
        registrar.newUnion("union")

        try {
            registrar.newEnum(name)
            Assert.fail("Expected an IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }

    /**
     * Test that adding a query does not check for duplicate names on all other types
     */
    @Test
    @Parameters("object", "interface", "enum", "union")
    fun testDuplicateNamesQuery(name: String) {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")
        registrar.newUnion("union")

        registrar.newQuery(name)
    }

    /**
     * Test getting a builder by it's name works regardless of type
     */
    @Test
    fun getBuilderByName() {
        val registrar = uk.co.grahamcox.graphql.builder.GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")
        registrar.newUnion("union")

        Assert.assertTrue(registrar.getTypeBuilder("object") is uk.co.grahamcox.graphql.builder.GraphQLObjectBuilder)
        Assert.assertTrue(registrar.getTypeBuilder("interface") is uk.co.grahamcox.graphql.builder.GraphQLObjectBuilder)
        Assert.assertTrue(registrar.getTypeBuilder("enum") is uk.co.grahamcox.graphql.builder.GraphQLEnumBuilder)
        Assert.assertTrue(registrar.getTypeBuilder("union") is uk.co.grahamcox.graphql.builder.GraphQLUnionBuilder)
        Assert.assertTrue(registrar.getTypeBuilder("other") == null)
    }
}