package uk.co.grahamcox.worldbuilder.webapp.graphql

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the GraphQL Type Name wrapper
 */
@RunWith(JUnitParamsRunner::class)
class GraphQLTypeNameTest {
    /**
     * Test successfully parsing a string
     * @param input The input string to parse
     * @param type The parsed raw type
     * @param nonNullable Whether the overall type is nullable
     * @param list Whether the overall type is a list
     * @param nonNullableList Whether the contents of the list are nullable
     */
    @Parameters(method = "paramsForSuccess")
    @Test
    fun testParseSuccess(input: String, type: String, nonNullable: Boolean, list: Boolean, nonNullableList: Boolean) {
        val result = GraphQLTypeName.parse(input)
        Assert.assertEquals("Type name doesn't match", type, result.typename)
        Assert.assertEquals("Nullability doesn't match", nonNullable, result.nonNullable)
        Assert.assertEquals("List doesn't match", list, result.list)
        Assert.assertEquals("List contents nullability doesn't match", nonNullableList, result.nonNullableList)
    }

    /**
     * Parameters for the success test
     */
    fun paramsForSuccess() = listOf(
            arrayOf("string", "string", false, false, false),
            arrayOf("string!", "string", true, false, false),
            arrayOf("[string]", "string", false, true, false),
            arrayOf("[string]!", "string", true, true, false),
            arrayOf("[string!]", "string", false, true, true),
            arrayOf("[string!]!", "string", true, true, true)
    )

    /**
     * Test that invalid names fail to parse
     * @param input The input string
     */
    @Parameters(method = "paramsForFailure")
    @Test(expected = IllegalArgumentException::class)
    fun testParseFailure(input: String) {
        GraphQLTypeName.parse(input)
    }

    /**
     * Parameters for the failure test
     */
    fun paramsForFailure() = listOf(
            "",
            "!",
            "[]",
            "[!]",
            "[]!",
            "[!]!",
            "123",
            "#hello",
            "hello world",
            "money$"
    )
}