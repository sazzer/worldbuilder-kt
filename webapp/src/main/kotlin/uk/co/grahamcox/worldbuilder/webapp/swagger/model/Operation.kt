package uk.co.grahamcox.worldbuilder.webapp.swagger.model

/**
 * Details of a single operation. This maps onto a single controller method
 * @property tags The tags of the operation
 * @property summary The summary description of the operation
 * @property responses The descriptions of the responses
 * @property parameters The parameters of the operation
 */
data class Operation(val tags: Array<String>? = null,
                     val summary: String = "",
                     val responses: Map<String, Any>,
                     val parameters: Array<Parameter> = arrayOf())