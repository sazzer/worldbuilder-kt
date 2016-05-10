package uk.co.grahamcox.worldbuilder.webapp

import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistrar
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistration

/**
 * Registration details for the error schema
 */
class ErrorSchemaRegistration : GraphQLRegistration {
    /**
     * Register the objects for returning errors from a handler
     * @param registrar The registrar to register the GraphQL schema with
     */
    override fun register(registrar: GraphQLRegistrar) {
        registrar.newObject("fieldError")
                .withDescription("An error that occurred with the data in a single field")
                .apply {
                    withField("errorCode")
                            .withDescription("The error code for the error that occurred")
                            .withType("id!")
                    withField("field")
                            .withDescription("The field that the error occurred on")
                            .withType("string!")
                    withField("message")
                            .withDescription("A friendly error message for developers")
                            .withType("string!")
                }

        registrar.newObject("globalError")
                .withDescription("An error that occurred with the request as a whole")
                .apply {
                    withField("errorCode")
                            .withDescription("The error code for the error that occurred")
                            .withType("id!")
                    withField("message")
                            .withDescription("A friendly error message for developers")
                            .withType("string!")
                }

        registrar.newObject("errors")
                .withDescription("Details of errors with this field")
                .apply {
                    withField("globalErrors")
                            .withDescription("Details of any global errors - i.e. ones that aren't specific to a single field")
                            .withType("[globalError!]!")
                    withField("fieldErrors")
                            .withDescription("Details of any field-specific errors")
                            .withType("[fieldError!]!")
                }


    }
}