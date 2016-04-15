package uk.co.grahamcox.worldbuilder.webapp.swagger.model

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Enumeration of the possible locations that parameters can be found
 * @property swaggerValue The value to use in Swagger
 */
enum class ParameterLocation(@get:JsonValue val swaggerValue: String) {
    PATH("path"),
    QUERYSTRING("query"),
    HEADER("header"),
    BODY("body"),
    FORM("form")
}