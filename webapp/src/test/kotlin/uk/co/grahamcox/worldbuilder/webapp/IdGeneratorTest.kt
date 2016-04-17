package uk.co.grahamcox.worldbuilder.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test
import uk.co.grahamcox.worldbuilder.service.worlds.WorldId
import java.util.*

/**
 * Unit tests for the ID Generator
 */
class IdGeneratorTest {
    private val WORLD_ID_ENCODED = "eyJuYW1lc3BhY2UiOiJXb3JsZElkIiwiaWQiOiJhYmNkZSJ9"

    private val WORLD_ID_BARE = "abcde"

    /** The object mapper to use */
    private val objectMapper = ObjectMapper()

    /** The test subject */
    private val testSubject = IdGenerator(objectMapper)

    /**
     * Test generating an ID from a Namespace and ID value
     */
    @Test
    fun testGenerateIdFromNamespace() {
        val generatedId = testSubject.generateId("WorldId", WORLD_ID_BARE)
        Assert.assertEquals(WORLD_ID_ENCODED, generatedId)
    }

    /**
     * Test generating an ID from an ID Type
     */
    @Test
    fun testGenerateIdFromId() {
        val generatedId = testSubject.generateId(WorldId(WORLD_ID_BARE))
        Assert.assertEquals(WORLD_ID_ENCODED, generatedId)
    }

    /**
     * Test parsing an ID from a Namespace and ID Value
     */
    @Test
    fun testParseIdFromNamespce() {
        val parsedId = testSubject.parseId("WorldId", WORLD_ID_ENCODED)
        Assert.assertEquals(WORLD_ID_BARE, parsedId)
    }

    /**
     * Test parsing an ID into an ID Type
     */
    @Test
    fun testParseIdFromClass() {
        val parsedId = testSubject.parseId(WORLD_ID_ENCODED, WorldId::class)
        Assert.assertEquals(WorldId(WORLD_ID_BARE), parsedId)
    }

    /**
     * Test parsing an ID where the provided ID is not base-64 encoded
     */
    @Test(expected = InvalidIdException::class)
    fun testParseNonBase64() {
        testSubject.parseId("WorldId", WORLD_ID_BARE)
    }

    /**
     * Test parsing an ID where the provided ID is not JSON
     */
    @Test(expected = InvalidIdException::class)
    fun testParseNonJson() {
        testSubject.parseId("WorldId", Base64.getMimeEncoder().encodeToString(WORLD_ID_BARE.toByteArray()))
    }

    /**
     * Test parsing an ID where the provided ID is a JSON object that isn't suitable
     */
    @Test(expected = InvalidIdException::class)
    fun testParseInvalidJson() {
        val json = "{}"
        testSubject.parseId("WorldId", Base64.getMimeEncoder().encodeToString(json.toByteArray()))
    }

    /**
     * Test parsing an ID where the provided ID is a JSON object that has extra fields
     */
    @Test(expected = InvalidIdException::class)
    fun testParseJsonExtraFields() {
        val json = "{\"namespace\": \"WorldId\", \"id\": \"abcde\", \"extra\": 1}"
        testSubject.parseId("WorldId", Base64.getMimeEncoder().encodeToString(json.toByteArray()))
    }

    /**
     * Test parsing an ID where the namespace is wrong
     */
    @Test(expected = InvalidIdException::class)
    fun testParseWrongNamespace() {
        testSubject.parseId("UserId", WORLD_ID_ENCODED)
    }


    /**
     * Test parsing an ID where the provided ID is perfectly valid but isn't what we'd generate
     */
    @Test
    fun testParseJsonAlternativeRepresentation() {
        val json = "{\"id\": \"abcde\", \"namespace\": \"WorldId\"}"
        val encoded = Base64.getMimeEncoder().encodeToString(json.toByteArray())
        Assert.assertNotEquals(WORLD_ID_ENCODED, encoded)

        val id = testSubject.parseId("WorldId", encoded)
        Assert.assertEquals(WORLD_ID_BARE, id)
    }
}