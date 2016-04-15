package uk.co.grahamcox.worldbuilder.webapp.swagger.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Descriptions of a parameter
 * @property name The name of the parameter
 * @property location The location of the parameter
 * @property description The description of the parameter
 * @property required Whether the parameter is required or not
 * @property type The type fo the parameter
 */
data class Parameter(val name: String,
                     @get:JsonProperty("in") val location: ParameterLocation,
                     val description: String? = null,
                     val required: Boolean = true,
                     val type: DataType)