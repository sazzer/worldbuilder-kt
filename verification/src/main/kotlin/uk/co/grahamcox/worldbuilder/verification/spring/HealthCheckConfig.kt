package uk.co.grahamcox.worldbuilder.verification.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient
import uk.co.grahamcox.worldbuilder.verification.healthcheck.HealthCheckFacade
import uk.co.grahamcox.worldbuilder.verification.healthcheck.HealthChecker

/**
 * The Spring Config for the health checks
 */
@Configuration
open class HealthCheckConfig {
    /**
     * The actual Health Checker
     */
    @Autowired
    @Bean
    open fun healthChecker(graphQLClient: GraphQLClient, objectMapper: ObjectMapper) =
            HealthChecker(graphQLClient, objectMapper)

    /**
     * The Health Check Facade layer
     */
    @Autowired
    @Bean
    @Scope("cucumber-glue")
    open fun healthCheckFacade(healthChecker: HealthChecker) = HealthCheckFacade(healthChecker)
}