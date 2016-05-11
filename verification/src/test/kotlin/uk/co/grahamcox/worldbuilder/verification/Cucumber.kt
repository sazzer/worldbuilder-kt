package uk.co.grahamcox.worldbuilder.verification

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

/**
 * Run all of the completed Cucumber tests
 */
@RunWith(Cucumber::class)
@CucumberOptions(tags = arrayOf("~@wip", "~@ignore"),
        format = arrayOf(
                "pretty",
                "html:target/site/cucumber/cucumber",
                "json:target/failsafe-reports/cucumber.json"
        ),
        strict = true)
class CucumberIT

/**
 * Run all of the Work in Progress Cucumber tests
 */
@RunWith(Cucumber::class)
@CucumberOptions(tags = arrayOf("@wip", "~@ignore"),
        format = arrayOf(
                "pretty",
                "html:target/site/cucumber/wip",
                "json:target/failsafe-reports/cucumberWip.json"
        ),
        strict = false)
class WipIT
