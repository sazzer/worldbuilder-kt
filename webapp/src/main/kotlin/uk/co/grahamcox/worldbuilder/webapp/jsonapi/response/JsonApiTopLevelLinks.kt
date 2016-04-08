package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Representation of the top-level links in a resource
 * @property self The self link
 * @property related The resource this relationship resource is for
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class JsonApiTopLevelLinks(val self: String,
                           val related: String? = null)