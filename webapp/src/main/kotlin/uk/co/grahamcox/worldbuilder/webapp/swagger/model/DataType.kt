package uk.co.grahamcox.worldbuilder.webapp.swagger.model

import com.fasterxml.jackson.annotation.JsonValue

/**
 * The Data Type to use for parameters
 * @property swaggerValue The value of the data type to use in Swagger
 */
enum class DataType(@get:JsonValue val swaggerValue: String) {
    INTEGER("integer"),
    NUMBER("number"),
    STRING("string"),
    BOOLEAN("boolean");
}