package uk.co.grahamcox.worldbuilder.webapp.swagger.model

/**
 * The Information about the API as a whole
 * @property title The title of the API
 * @property version The version of the API
 */
data class Info(val title: String,
                val version: String)