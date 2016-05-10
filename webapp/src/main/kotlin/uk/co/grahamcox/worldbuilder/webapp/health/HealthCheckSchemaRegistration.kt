package uk.co.grahamcox.worldbuilder.webapp.health

import uk.co.grahamcox.graphql.builder.GraphQLRegistrar
import uk.co.grahamcox.graphql.builder.GraphQLRegistration

class HealthCheckSchemaRegistration : GraphQLRegistration {
    override fun register(registrar: GraphQLRegistrar) {
        registrar.newEnum("healthCheckStatus")
                .withDescription("The status of an individual health check")
                .withMembers(HealthCheckStatus::class)

        registrar.newObject("healthCheck")
                .withDescription("The result of a single health check")
                .apply {
                    withField("name")
                            .withDescription("The name of the health check")
                            .withType("string!")
                    withField("status")
                            .withDescription("The status of the health check")
                            .withType("healthCheckStatus!")
                    withField("message")
                            .withDescription("A message about the health check result")
                            .withType("string")
                }

        registrar.newQuery("healthCheck")
                .withDescription("Actually perform the health checks and get the results")
                .withType("[healthCheck!]!")
                .withFetcher(HealthCheckFetcher())
    }
}