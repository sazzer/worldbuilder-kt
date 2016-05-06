package uk.co.grahamcox.worldbuilder.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory

/**
 * Data Fetcher for Mutations.
 * This handles all of the common details and delegates off to a strategy for the actual work
 * @property handler The handler to use
 * @property inputClass The input class to convert the input values to
 * @property objectMapper The name under which to put the output value into
 * @property outputName The object mapper to use to convert the input values
 */
class MutationFetcher<IN, OUT>(private val handler: MutationHandler<IN, OUT>,
                               private val inputClass: Class<IN>,
                               private val outputName: String,
                               private val objectMapper: ObjectMapper) : DataFetcher {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(MutationFetcher::class.java)
    }
    /**
     * Handler for actually doing the mutation
     */
    interface MutationHandler<IN, OUT> {
        /**
         * Process the provided Input model
         * @param input The input to process
         * @return the result of the mutation
         */
        fun process(input: IN, environment: DataFetchingEnvironment) : OUT?
    }

    override fun get(environment: DataFetchingEnvironment): Map<String, Any?> {
        val input = environment.arguments["input"]

        return when (input) {
            null -> throw IllegalStateException("No user details were provided")
            !is Map<*, *> -> throw IllegalStateException("User details were not a valid object")
            else -> {
                LOG.debug("Performing mutation {} with input {}", environment, input)
                val parsedInput = objectMapper.convertValue(input, inputClass)
                val result = handler.process(parsedInput, environment)

                LOG.debug("Performed mutation {} with input {} giving result {}", environment, input, result)
                mapOf(
                        outputName to result,
                        "clientMutationId" to input["clientMutationId"]
                )
            }
        }
    }
}