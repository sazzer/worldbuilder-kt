package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Representation of the links in a relationship
 * @property self The self link
 * @property related The related link
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class JsonApiRelationshipLinks(val self: String?,
                               val related: String)