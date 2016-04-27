package uk.co.grahamcox.worldbuilder.spring

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

open class DatabaseConfig {
    /** The MongoDB URL to use */
    @Value("\${mongo.url}")
    lateinit var mongoUrl: String

    /** The name of the MongoDB Database to use */
    @Value("\${mongo.db}")
    lateinit var mongoDb: String

    /** The MongoDB Client to use */
    @Autowired
    @Bean
    open fun mongoClient() = MongoClient(MongoClientURI(mongoUrl))

    /** The MongoDB Database to use */
    @Autowired
    @Bean
    open fun mongoDatabase(mongoClient: MongoClient) = mongoClient.getDatabase(mongoDb)
}