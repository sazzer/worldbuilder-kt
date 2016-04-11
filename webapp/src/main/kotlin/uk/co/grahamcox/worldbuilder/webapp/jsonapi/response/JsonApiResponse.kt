package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Representation of a JSON API Response
 * @param <T> The type of payload to use for the data
 * @property links The top level links in the response
 * @property data The data in the response
 * @property included Any included resources
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class JsonApiResponse<T>(val links: JsonApiTopLevelLinks,
                      val data: T,
                      val included: List<JsonApiResource>? = null)