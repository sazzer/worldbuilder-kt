package uk.co.grahamcox.worldbuilder.webapp.graphql

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
        val registrar = GraphQLRegistrar()

        val builder = registrar.newObject("obj")

        Assert.assertEquals(1, registrar.objectBuilders.size)
        Assert.assertEquals(builder, registrar.objectBuilders["obj"])
        Assert.assertTrue(registrar.enumBuilders.isEmpty())
        Assert.assertTrue(registrar.interfaceBuilders.isEmpty())
        Assert.assertTrue(registrar.queryBuilders.isEmpty())
    }

    /**
     * Test adding a new interface
     */
    @Test
    fun testNewInterface() {
        val registrar = GraphQLRegistrar()

        val builder = registrar.newInterface("obj")

        Assert.assertEquals(1, registrar.interfaceBuilders.size)
        Assert.assertEquals(builder, registrar.interfaceBuilders["obj"])
        Assert.assertTrue(registrar.enumBuilders.isEmpty())
        Assert.assertTrue(registrar.objectBuilders.isEmpty())
        Assert.assertTrue(registrar.queryBuilders.isEmpty())
    }

    /**
     * Test adding a new enum
     */
    @Test
    fun testNewEnum() {
        val registrar = GraphQLRegistrar()

        val builder = registrar.newEnum("obj")

        Assert.assertEquals(1, registrar.enumBuilders.size)
        Assert.assertEquals(builder, registrar.enumBuilders["obj"])
        Assert.assertTrue(registrar.objectBuilders.isEmpty())
        Assert.assertTrue(registrar.interfaceBuilders.isEmpty())
        Assert.assertTrue(registrar.queryBuilders.isEmpty())
    }

    /**
     * Test adding a new query
     */
    @Test
    fun testNewQuery() {
        val registrar = GraphQLRegistrar()

        val builder = registrar.newQuery("obj")

        Assert.assertEquals(1, registrar.queryBuilders.size)
        Assert.assertEquals(builder, registrar.queryBuilders["obj"])
        Assert.assertTrue(registrar.enumBuilders.isEmpty())
        Assert.assertTrue(registrar.interfaceBuilders.isEmpty())
        Assert.assertTrue(registrar.objectBuilders.isEmpty())
    }

    /**
     * Test that adding an object checks for duplicate names on all other types
     */
    @Test
    @Parameters("object", "interface", "enum")
    fun testDuplicateNamesObject(name: String) {
        val registrar = GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")

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
    @Parameters("object", "interface", "enum")
    fun testDuplicateNamesInterface(name: String) {
        val registrar = GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")

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
    @Parameters("object", "interface", "enum")
    fun testDuplicateNamesEnum(name: String) {
        val registrar = GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")

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
    @Parameters("object", "interface", "enum")
    fun testDuplicateNamesQuery(name: String) {
        val registrar = GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")

        registrar.newQuery(name)
    }

    /**
     * Test getting a builder by it's name works regardless of type
     */
    @Test
    fun getBuilderByName() {
        val registrar = GraphQLRegistrar()

        registrar.newObject("object")
        registrar.newInterface("interface")
        registrar.newEnum("enum")

        Assert.assertTrue(registrar.getTypeBuilder("object") is GraphQLObjectBuilder)
        Assert.assertTrue(registrar.getTypeBuilder("interface") is GraphQLObjectBuilder)
        Assert.assertTrue(registrar.getTypeBuilder("enum") is GraphQLEnumBuilder)
        Assert.assertTrue(registrar.getTypeBuilder("other") == null)
    }
}