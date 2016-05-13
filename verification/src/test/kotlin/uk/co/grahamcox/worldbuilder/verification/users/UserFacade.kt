package uk.co.grahamcox.worldbuilder.verification.users

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.verification.ModelPopulator
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient

/**
 * Facade layer for working with users
 * @property graphQLClient The GraphQL Client
 * @property objectMapper The Object Mapper
 */
class UserFacade(private val graphQLClient: GraphQLClient,
                 private val objectMapper: ObjectMapper) {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(ModelPopulator::class.java)

        /** GraphQL Query to use to create a new user */
        val CREATE_USER_QUERY = """
            mutation newUser(${'$'}name:String!, ${'$'}email:String!) {
              createUser(input: {name: ${'$'}name, email: ${'$'}email}) {
                __typename,
                ... on user {
                  id,
                  name,
                  email,
                  created,
                  updated,
                  enabled,
                  verified
                }
                ... on errors {
                  globalErrors {
                    errorCode
                  }
                  fieldErrors {
                    errorCode,
                    field
                  }
                }
              }
            }
        """
    }

    /** Result of creating a user */
    private var createdUserDetails: Any? = null

    /**
     * Attempt to create a user with the given details
     * @param details The details of the user to create
     */
    fun createUser(details: NewUserModel) {
        LOG.info("Attempting to create new user: {}", details)
        val userResponse = graphQLClient.performQuery(CREATE_USER_QUERY, details)
        val createUserResponse = userResponse["createUser"]
        when (createUserResponse) {
            null -> throw IllegalStateException("No createUser Response returned")
            !is Map<*, *> -> throw IllegalStateException("createUser Response not of expected type")
            else -> {
                LOG.debug("Received response: {}", createUserResponse)
                val responseType = createUserResponse["__typename"]
                when (responseType) {
                    "user" -> {
                        LOG.debug("New user created successfully")
                        createdUserDetails = objectMapper.convertValue(createUserResponse,
                                UserDetailsResponse::class.java)
                    }
                    "errors" -> {
                        LOG.debug("Failed to create new user")
                        throw IllegalStateException("Error occurred creating user")
                    }
                    else -> throw IllegalStateException("Unexpected response type: ${responseType}")
                }
            }
        }
    }

    /**
     * Check that we have attempted to create a user and the attempt was a success
     * @return True if we have created a user successfully. False if not
     */
    fun userWasCreatedSuccessfully() = createdUserDetails != null && createdUserDetails is UserDetailsResponse
}