package uk.co.grahamcox.worldbuilder.webapp.health

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

/**
 * GraphQL Data Fetcher to perform health checks
 */
class HealthCheckFetcher : DataFetcher {
    /**
     * Actually perform the health checks and get the results
     * @param environment The execution environment
     * @return the results of the health checks
     */
    override fun get(environment: DataFetchingEnvironment): List<HealthCheckModel> {
        return listOf(
                HealthCheckModel(
                        name = "Simple",
                        status = HealthCheckStatus.OK.name,
                        message = null
                )
        )
    }
}