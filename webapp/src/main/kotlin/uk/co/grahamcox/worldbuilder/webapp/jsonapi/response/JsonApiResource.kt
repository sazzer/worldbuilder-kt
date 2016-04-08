package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Representation of the Data in a JSON API Response, when there is only a single element
 * @property type The type of the resource
 * @property id The ID of the resource
 * @property attributes The attributes of the resource
 * @property relationships The relationships of the resource
 * @property links The resource level links
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class JsonApiResource(val type: String,
                      val id: Any,
                      val attributes: Map<String, Any>,
                      val relationships: Map<String, JsonApiRelationship>? = null,
                      val links: JsonApiResourceLinks? = null)