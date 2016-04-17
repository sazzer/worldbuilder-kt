package uk.co.grahamcox.worldbuilder.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.Id
import java.util.*
import kotlin.reflect.KClass

/**
 * Exception to throw if we try to decode an invalid ID
 * @param message The error message
 */
class InvalidIdException(message: String) : Exception(message)

/**
 * Mechanism to generate namespaced IDs for the API
 * @property objectMapper The Object Mapper to use
 */
class IdGenerator(private val objectMapper: ObjectMapper) {
    companion object {
        private val LOG = LoggerFactory.getLogger(IdGenerator::class.java)
    }

    /**
     * Generate an ID that is in the given namespace
     * @param namespace The namespace to use
     * @param id The ID to use
     * @return the namespaced and encoded ID
     */
    fun generateId(namespace: String, id: String) : String {
        val idObject = mapOf(
                "namespace" to namespace,
                "id" to id
        )

        val json = objectMapper.writeValueAsBytes(idObject)

        val encoded = Base64.getMimeEncoder().encodeToString(json)

        LOG.debug("Encoded ID {} as {}", idObject, encoded)
        return encoded
    }

    /**
     * Generate an ID that represents the given ID object, using the class name as the namespace
     * @param id The ID to use
     * @return the namespaced and encoded ID
     */
    fun generateId(id: Id) = generateId(id.javaClass.simpleName, id.id)

    /**
     * Attempt to parse the given ID String and return the raw ID that it represents
     * This uses the class name of the
     */
    fun <T : Id> parseId(id: String, idType: KClass<T>) : T{
        val namespace = idType.java.simpleName
        val parsedId = parseId(namespace, id)
        val builtId = idType.java.getDeclaredConstructor(String::class.java).newInstance(parsedId)
        LOG.debug("Constructed ID {} of type {} from id {}", builtId, idType, id)
        return builtId
    }

    /**
     * Attempt to parse the given ID string and return the raw ID that it represents
     * @param namespace The namespace of the ID
     * @param id the enoded ID string, which must be in the given namespace
     * @return the raw ID
     */
    fun parseId(namespace: String, id: String) : String {
        val decoded = try {
            Base64.getMimeDecoder().decode(id)
        } catch (e: IllegalArgumentException) {
            LOG.warn("Provided ID was not correctly Base64 encoded: {}", id, e)
            throw InvalidIdException("Provided ID is not correctly encoded")
        }

        val idObject = try {
            objectMapper.readValue<Map<String, String>>(decoded)
        } catch (e: Exception) {
            LOG.warn("Failed to parse the JSON string decoded: {}", String(decoded), e)
            throw InvalidIdException("Provided ID did not parse correctly")
        }


        val parsedId = idObject["id"]
        val parsedNamespace = idObject["namespace"]
        if (parsedNamespace == null) {
            LOG.warn("Parsed JSON did not contain a 'namespace' field': {}", idObject)
            throw InvalidIdException("Provided ID did not have correct fields")
        } else if (parsedId == null) {
            LOG.warn("Parsed JSON did not contain an 'id' field': {}", idObject)
            throw InvalidIdException("Provided ID did not have correct fields")
        } else if (idObject.keys.size != 2) {
            LOG.warn("Parsed JSON had more fields than expected: {}", idObject)
            throw InvalidIdException("Provided ID did not have correct fields")
        } else if (!idObject["namespace"].equals(namespace)) {
            LOG.warn("Parsed JSON had a namespace of {} instead of {}", idObject["namespace"], namespace)
            throw InvalidIdException("Provided ID did not have correct namespace")
        }

        LOG.debug("Successfully parsed ID {} as being ID {} in namespace {}", id, parsedId, namespace)

        return parsedId
    }
}