package uk.co.grahamcox.worldbuilder.spring

import com.mongodb.client.MongoDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.service.users.DaoUserEditor
import uk.co.grahamcox.worldbuilder.service.users.DaoUserFinder
import uk.co.grahamcox.worldbuilder.service.users.dao.UserDao
import uk.co.grahamcox.worldbuilder.service.users.dao.UserMongoDao
import java.time.Clock

/**
 * Spring Configuration for the Users service
 */
@Configuration
open class UsersConfig {
    /**
     * The User DAO
     */
    @Autowired
    @Bean
    open fun userDao(mongoDatabase: MongoDatabase, clock: Clock) = UserMongoDao(
            collection = mongoDatabase.getCollection("users"),
            clock = clock
    )

    /**
     * The User Finder
     */
    @Autowired
    @Bean
    open fun userFinder(userDao: UserDao) = DaoUserFinder(userDao)

    /**
     * The User Editor
     */
    @Autowired
    @Bean
    open fun userEditor(userDao: UserDao) = DaoUserEditor(userDao)
}