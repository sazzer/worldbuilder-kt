package uk.co.grahamcox.worldbuilder.webapp.swagger.model

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * Representation of the response of an operation
 * @property description The location of the schema
 */
class Response(private val schemaLocation: String) {
    val description = "The Schema"

    fun getSchema(): Map<String, Any> {
        val schemaPath = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(schemaLocation).build().toUriString()
        return mapOf("\$ref" to schemaPath)
    }

}
