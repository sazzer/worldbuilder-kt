package uk.co.grahamcox.worldbuilder.verification.graphql

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Response received from a GraphQL Request
 * @property data The actual data received
 * @property errors Any errors received
 */
data class GraphQLResponse @JsonCreator constructor(
        @JsonProperty("data") val data: Map<String, Any?>,
        @JsonProperty("errors") val errors: List<Any?>
)