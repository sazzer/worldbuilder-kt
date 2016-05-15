package uk.co.grahamcox.worldbuilder.verification.steps

import cucumber.api.DataTable
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import uk.co.grahamcox.worldbuilder.verification.DataTableParser
import uk.co.grahamcox.worldbuilder.verification.populator.ModelPopulator
import uk.co.grahamcox.worldbuilder.verification.Result
import uk.co.grahamcox.worldbuilder.verification.comparitor.SingleModelComparator
import uk.co.grahamcox.worldbuilder.verification.users.NewUserModel
import uk.co.grahamcox.worldbuilder.verification.users.UserFacade

/**
 * Steps used to manage a users details
 */
class UserManagementSteps {
    /** Mechanism to populate New User details from a data table */
    @Autowired
    private lateinit var newUserPopulator: ModelPopulator

    /** Mechanism to compare a user to the expected values */
    @Autowired
    private lateinit var userDetailsComparator: SingleModelComparator

    /** Mechanism to work with user details */
    @Autowired
    private lateinit var userFacade: UserFacade

    /**
     * Attempt to create a user record
     */
    @When("^I create a user with the details:$")
    fun createUser(userDetails: DataTable) {
        val parsedUserDetails = DataTableParser.parseSingleTall(userDetails)
        val newUserDetails = newUserPopulator.populate(parsedUserDetails, NewUserModel::class.java)
        userFacade.createUser(newUserDetails)
    }

    /**
     * Assert that the user was created successfully
     */
    @Then("^the user was created successfully$")
    fun userCreatedSuccessfully() {
        Assert.assertNotNull(userFacade.createdUserDetails)
        Assert.assertTrue("Expected user creation to have succeeded", userFacade.createdUserDetails is Result.Success)
    }

    /**
     * If we created a user successfully then check that the deatils are correct
     */
    @Then("^the created user details are:$")
    fun checkCreatedUser(userDetails: DataTable) {
        val parsedUserDetails = DataTableParser.parseSingleTall(userDetails)
        Assert.assertNotNull(userFacade.createdUserDetails)
        Assert.assertTrue("Expected user creation to have failed", userFacade.createdUserDetails is Result.Success)

        val createUserResult = userFacade.createdUserDetails as Result.Success
        val missedMatches = userDetailsComparator.compare(parsedUserDetails, createUserResult.value)
        if (missedMatches.isNotEmpty()) {
            Assert.fail("Failed to match entries: ${missedMatches}");
        }
    }

    /**
     * Assert that the user creation failed with some global errors
     */
    @Then("^user creation failed with the errors:$")
    fun userCreationFailedGlobalErrors(errors: List<String>) {
        Assert.assertNotNull(userFacade.createdUserDetails)
        Assert.assertTrue("Expected user creation to have failed", userFacade.createdUserDetails is Result.Failure)

        val createUserResult = userFacade.createdUserDetails as Result.Failure
        Assert.assertTrue("No Global errors occurred", createUserResult.value.globalErrors.isNotEmpty())
    }

    /**
     * Assert that the user creation failed with some global errors
     */
    @Then("^user creation failed with the field errors:$")
    fun userCreationFailedFieldErrors(errors: DataTable) {
        val errorDetails = DataTableParser.parseSingleTall(errors)

        Assert.assertNotNull(userFacade.createdUserDetails)
        Assert.assertTrue("Expected user creation to have failed", userFacade.createdUserDetails is Result.Failure)

        val createUserResult = userFacade.createdUserDetails as Result.Failure
        Assert.assertTrue("No Field errors occurred", createUserResult.value.fieldErrors.isNotEmpty())
    }
}