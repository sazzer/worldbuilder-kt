package uk.co.grahamcox.worldbuilder.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory

/**
 * Data Fetcher for Mutations.
 * This handles all of the common details and delegates off to a strategy for the actual work
 */
class MutationFetcher<IN, OUT>(private val handler: MutationHandler<IN, OUT>,
                      private val objectMapper: ObjectMapper) : DataFetcher {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(MutationFetcher::class.java)
    }
    /**
     * Handler for actually doing the mutation
     */
    interface MutationHandler<IN, OUT> {
        /** The Input Model type to use */
        val inputModel: Class<IN>

        /**
         * Process the provided Input model
         * @param input The input to process
         * @return the result of the mutation
         */
        fun process(input: IN, environment: DataFetchingEnvironment) : OUT?
    }

    override fun get(environment: DataFetchingEnvironment): Any? {
        val input = environment.arguments["input"]

        return when (input) {
            null -> throw IllegalStateException("No user details were provided")
            !is Map<*, *> -> throw IllegalStateException("User details were not a valid object")
            else -> {
                LOG.debug("Performing mutation {} with input {}", environment, input)
                val parsedInput = objectMapper.convertValue(input, handler.inputModel)
                val result = handler.process(parsedInput, environment)

                LOG.debug("Performed mutation {} with input {} giving result {}", environment, input, result)
                result
            }
        }
    }
}