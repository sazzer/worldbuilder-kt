package uk.co.grahamcox.worldbuilder.webapp.swagger.model

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * Representation of the response of an operation
 * @property description The description of the response
 * @param schemaLocation The location of the schema
 */
class Response(val description: String,
               private val schemaLocation: String? = null) {

    fun getSchema(): Map<String, Any>? {
        return if (schemaLocation != null) {
            val schemaPath = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replacePath(schemaLocation).build().toUriString()
            mapOf("\$ref" to schemaPath)
        } else {
            null
        }
    }

}
