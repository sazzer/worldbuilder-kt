package uk.co.grahamcox.worldbuilder.verification.users

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.verification.ErrorsResponse
import uk.co.grahamcox.worldbuilder.verification.Result
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient

/**
 * Mechanism by which we can call the API to create users
 * @property graphQLClient The GraphQL Client
 * @property objectMapper The Object Mapper
 */
class UserCreator(private val graphQLClient: GraphQLClient,
                  private val objectMapper: ObjectMapper) {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(UserCreator::class.java)

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

    /**
     * Attempt to create a user with the given details
     * @param details The details of the user to create
     */
    fun createUser(details: NewUserModel): Result<UserDetailsResponse, ErrorsResponse> {
        LOG.info("Attempting to create new user: {}", details)
        val userResponse = graphQLClient.performQuery(CREATE_USER_QUERY, details)
        val createUserResponse = userResponse["createUser"]
        return when (createUserResponse) {
            null -> throw IllegalStateException("No createUser Response returned")
            !is Map<*, *> -> throw IllegalStateException("createUser Response not of expected type")
            else -> {
                LOG.debug("Received response: {}", createUserResponse)
                val responseType = createUserResponse["__typename"]
                when (responseType) {
                    "user" -> {
                        LOG.debug("New user created successfully")
                        val userDetails = objectMapper.convertValue(createUserResponse,
                                UserDetailsResponse::class.java)
                        Result.Success(userDetails)
                    }
                    "errors" -> {
                        LOG.debug("Failed to create new user")
                        val errorDetails = objectMapper.convertValue(createUserResponse,
                                ErrorsResponse::class.java)
                        Result.Failure(errorDetails)
                    }
                    else -> throw IllegalStateException("Unexpected response type: ${responseType}")
                }
            }
        }
    }
}