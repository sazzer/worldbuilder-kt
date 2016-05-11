package uk.co.grahamcox.worldbuilder.verification.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.net.URI

/**
 * The mechanism by which GraphQL calls will be sent to the server
 * @property restTemplate The REST Template to make the calls with
 * @property graphQlUrl The URL to call
 */
class GraphQLClient(private val restTemplate: RestTemplate,
                    private val graphQlUrl: URI) {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(GraphQLClient::class.java)
    }

    /**
     * Actually perform the query provided and get the response back
     * @param query The query to perform
     * @param responseClass The class to use for the response
     */
    fun performQuery(query: String): Map<String, Any?> {
        LOG.debug("Sending query {} to {}", query, graphQlUrl)

        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("application/graphql")
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val request = RequestEntity<String>(query, headers, HttpMethod.POST, graphQlUrl)

        val response = restTemplate.exchange(request, GraphQLResponse::class.java)
        LOG.debug("Sent request {} and got response {}", request, response)

        return if (response.statusCode == HttpStatus.OK) {
            val errors = response.body.errors
            val data = response.body.data
            if (errors.isNotEmpty()) {
                // We got a GraphQL error
                throw IllegalStateException("A GraphQL Error occurred")
            } else {
                data
            }
        } else {
            // We got an HTTP Error
            throw IllegalStateException("An HTTP Error occurred")
        }
    }
}