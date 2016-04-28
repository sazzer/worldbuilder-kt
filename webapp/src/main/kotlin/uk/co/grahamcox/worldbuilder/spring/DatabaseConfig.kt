package uk.co.grahamcox.worldbuilder.spring

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile

/**
 * Spring configuration for the Mongo Database
 */
open class DatabaseConfig {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(DatabaseConfig::class.java)
    }

    /** The MongoDB URL to use */
    @Value("\${mongo.url}")
    lateinit var mongoUrl: String

    /** The name of the MongoDB Database to use */
    @Value("\${mongo.db}")
    lateinit var mongoDb: String

    /** The URL of the Embedded MongoDB */
    var embeddedMongoUrl: String? = null
    /**
     * Start an embedded Mongo Database
     */
    @Bean(name = arrayOf("embeddedMongoDatabase"), destroyMethod = "stop")
    @Profile("dev")
    open fun embeddedMongoDatabase(): MongodExecutable? {
        LOG.debug("Starting an embedded mongo database")
        val starter = MongodStarter.getDefaultInstance()
        val networkConfig = Net(Network.getFreeServerPort(),
                Network.localhostIsIPv6())
        val config = MongodConfigBuilder()
                .version(Version.Main.V3_2)
                .net(networkConfig)
                .build()
        val executable = starter.prepare(config)
        executable.start()

        embeddedMongoUrl = "mongodb://${networkConfig.serverAddress.hostAddress}:${networkConfig.port}"
        return executable
    }

    @Bean(name = arrayOf("embeddedMongoDatabase"))
    @Profile("!dev")
    open fun noEmbeddedMongoDatabase(): Any? {
        LOG.debug("Not starting an embedded mongo database")
        return null
    }

    /** The MongoDB Client to use */
    @Bean
    @DependsOn("embeddedMongoDatabase")
    open fun mongoClient(): MongoClient {
        val realUrl = embeddedMongoUrl ?: mongoUrl
        LOG.debug("Connecting to MongoDB on {}", realUrl)
        return MongoClient(MongoClientURI(realUrl))
    }

    /** The MongoDB Database to use */
    @Autowired
    @Bean
    open fun mongoDatabase(mongoClient: MongoClient) = mongoClient.getDatabase(mongoDb)
}