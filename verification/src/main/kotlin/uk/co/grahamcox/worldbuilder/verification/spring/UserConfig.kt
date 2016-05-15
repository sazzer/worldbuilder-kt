package uk.co.grahamcox.worldbuilder.verification.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.verification.DataTableEntry
import uk.co.grahamcox.worldbuilder.verification.FieldError
import uk.co.grahamcox.worldbuilder.verification.comparitor.CollectionComparator
import uk.co.grahamcox.worldbuilder.verification.comparitor.DateTimeComparator
import uk.co.grahamcox.worldbuilder.verification.comparitor.FieldPath
import uk.co.grahamcox.worldbuilder.verification.comparitor.SingleModelComparator
import uk.co.grahamcox.worldbuilder.verification.errors.FieldErrorComparison
import uk.co.grahamcox.worldbuilder.verification.populator.ModelPopulator
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient
import uk.co.grahamcox.worldbuilder.verification.users.UserCreator
import uk.co.grahamcox.worldbuilder.verification.users.UserFacade
import java.time.Clock

/**
 * Spring config for working with users
 */
@Configuration
open class UserConfig {
    /**
     * Populator to populate new user objects
     */
    @Bean
    open fun newUserPopulator() = ModelPopulator(mapOf(
            "Name" to "name",
            "Email" to "email"
    ))

    /**
     * Comparator for user details objects
     */
    @Autowired
    @Bean
    open fun userDetailsComparator(clock: Clock) = SingleModelComparator(mapOf(
            "Name" to FieldPath("name"),
            "Email" to FieldPath("email"),
            "Created" to FieldPath("created"),
            "Updated" to FieldPath("updated"),
            "Enabled" to FieldPath("enabled"),
            "Verified" to FieldPath("verified"),
            "Created" to FieldPath("created", DateTimeComparator(clock)),
            "Updated" to FieldPath("updated", DateTimeComparator(clock))
    ))

    /**
     * Comparator for matching up field errors
     */
    @Bean
    open fun fieldErrorComparator() = CollectionComparator<DataTableEntry, FieldError>(FieldErrorComparison(mapOf(
            "Email" to "email",
            "Name" to "name"
    )))

    /**
     * Mechanism for creating users
     */
    @Autowired
    @Bean
    open fun userCreator(graphQLClient: GraphQLClient, objectMapper: ObjectMapper) =
            UserCreator(graphQLClient, objectMapper)

    /**
     * Facade for working with users
     */
    @Autowired
    @Bean
    open fun userFacade(userCreator: UserCreator) = UserFacade(userCreator)
}