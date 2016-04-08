package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Representation of a Resource Identifier in a JSON API Response
 * @property type The type of the resource
 * @property id The ID of the resource
 */
class JsonApiResourceIdentifier(val type: String,
                                val id: Any)