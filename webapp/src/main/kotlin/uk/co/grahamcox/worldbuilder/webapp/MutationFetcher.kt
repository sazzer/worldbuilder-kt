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
 * @property validators The validators to use for validating the request
 */
class MutationFetcher<IN, OUT>(private val handler: MutationHandler<IN, OUT>,
                               private val inputClass: Class<IN>,
                               private val objectMapper: ObjectMapper,
                               private val validators: Collection<RequestValidator<IN>>) : DataFetcher {
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

    /**
     * Actually perform the mutation. This does the work of converting the input to the correct type, validatign it and
     * then delegates to the handler to do the actual processing
     */
    override fun get(environment: DataFetchingEnvironment): Any? {
        val input = environment.arguments["input"]

        return when (input) {
            null -> throw IllegalStateException("No user details were provided")
            !is Map<*, *> -> throw IllegalStateException("User details were not a valid object")
            else -> {
                LOG.debug("Performing mutation {} with input {}", environment, input)
                val parsedInput = objectMapper.convertValue(input, inputClass)
                val validationErrors = validators.flatMap { v -> v.validate(parsedInput) }
                val result = if (validationErrors.isNotEmpty()) {
                    val fieldErrors = validationErrors.filter { e -> e is FieldError }
                            .map { e -> e as FieldError }
                    val globalErrors = validationErrors.filter { e -> e is GlobalError }
                            .map { e -> e as GlobalError }
                    LOG.debug("Validation errors occurred. Global errors = {}, Field errors = {}", globalErrors, fieldErrors)
                    ErrorResponseModel(globalErrors = globalErrors, fieldErrors = fieldErrors)
                } else {
                    try {
                        handler.process(parsedInput, environment)
                    } catch (e: FetcherException) {
                        ErrorResponseModel(globalErrors = e.globalErrors, fieldErrors = e.fieldErrors)
                    }
                }

                LOG.debug("Performed mutation {} with input {} giving result {}", environment, input, result)
                result
            }
        }
    }
}