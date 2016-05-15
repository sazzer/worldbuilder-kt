package uk.co.grahamcox.worldbuilder.verification.steps

import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.springframework.beans.factory.annotation.Autowired
import uk.co.grahamcox.worldbuilder.verification.healthcheck.HealthCheckFacade

/**
 * Cucumber steps to work with the health checks
 */
class HealthcheckSteps {
    /** The mechanism by which we can check the health of the server */
    @Autowired
    private lateinit var healthCheckFacade: HealthCheckFacade

    /**
     * Perform the health check
     */
    @When("^I request the health check status$")
    fun performHealthCheck() {
        healthCheckFacade.perform()
    }

    /**
     * Check that we have performed the healthchecks and they passed
     */
    @Then("^all health checks pass$")
    fun checkHealthChecksPass() {
        healthCheckFacade.assertAllOk()
    }
}