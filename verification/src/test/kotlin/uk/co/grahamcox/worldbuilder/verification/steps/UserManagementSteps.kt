package uk.co.grahamcox.worldbuilder.verification.steps

import cucumber.api.DataTable
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import uk.co.grahamcox.worldbuilder.verification.DataTableParser
import uk.co.grahamcox.worldbuilder.verification.ModelPopulator
import uk.co.grahamcox.worldbuilder.verification.users.NewUserModel
import uk.co.grahamcox.worldbuilder.verification.users.UserFacade

/**
 * Steps used to manage a users details
 */
class UserManagementSteps {
    /** Mechanism to populate New User details from a data table */
    @Autowired
    private lateinit var newUserPopulator: ModelPopulator

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
        Assert.assertTrue(userFacade.userWasCreatedSuccessfully())
    }

    /**
     * If we created a user successfully then check that the deatils are correct
     */
    @Then("^the created user details are:$")
    fun checkCreatedUser(userDetails: DataTable) {

    }

    /**
     * Assert that the user creation failed
     */
    @Then("^user creation failed with the error \"(.*)\"$")
    fun userCreationFailed(error: String) {

    }
}