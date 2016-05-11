package uk.co.grahamcox.worldbuilder.verification.healthcheck

import org.junit.Assert

/**
 * Facade wrapper around performing healthchecks
 * @property healthChecker The mechanism to actually perform the health checks
 */
class HealthCheckFacade(private val healthChecker: HealthChecker) {
    /** The health check responses that we've received */
    private var healthCheckResponse: List<HealthCheckResponse>? = null

    /**
     * Actually perform the healthchecks against the server
     */
    fun perform() {
        healthCheckResponse = healthChecker.performHealthcheck()
    }

    /**
     * Assert that the result of the last healthcheck was all OK
     */
    fun assertAllOk() {
        Assert.assertNotNull("No Health Check Response has been received", healthCheckResponse)

        healthCheckResponse?.apply {
            Assert.assertTrue("The Health Check Response contained no entries", isNotEmpty())

            val errorResponses = filter { it.status != "OK" }
            if (errorResponses.isNotEmpty()) {
                Assert.fail("The following health checks were not OK: ${errorResponses}")
            }

        }
    }
}