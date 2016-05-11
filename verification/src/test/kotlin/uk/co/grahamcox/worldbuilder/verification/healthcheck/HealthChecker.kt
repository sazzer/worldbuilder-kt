package uk.co.grahamcox.worldbuilder.verification.healthcheck

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient

/**
 * Mechanism to perform a healthcheck on the server
 * @property graphQLClient The GraphQL CLient to use
 * @property objectMapper The Object Mapper to use
 */
class HealthChecker(private val graphQLClient: GraphQLClient,
                    private val objectMapper: ObjectMapper) {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(HealthChecker::class.java)

        /** The Healthcheck Query to execute */
        private val HEALTHCHECK_QUERY = """{
          healthCheck {
            name,
            status,
            message
          }
        }"""
    }

    /**
     * Perform the healthcheck on the server and return the results
     * @return the results of the healthcheck
     */
    fun performHealthcheck(): List<HealthCheckResponse> {
        val response = graphQLClient.performQuery(HEALTHCHECK_QUERY)["healthCheck"]
        val healthCheckResponses = when (response) {
            null -> throw IllegalStateException("The Health Check response was null")
            !is List<*> -> throw IllegalStateException("The Health Check response was not a list as expected")
            else -> response.map { objectMapper.convertValue(it, HealthCheckResponse::class.java) }
        }
        LOG.debug("Healthcheck response: {}", healthCheckResponses)

        return healthCheckResponses
    }
}