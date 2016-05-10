package uk.co.grahamcox.dao.embedded.mongodb

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.bson.Document
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.slf4j.LoggerFactory
import java.util.*

/**
 * JUnit Rule for running a MongoDB database for tests
 */
class EmbeddedMongoRule : TestRule {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(EmbeddedMongoRule::class.java)
    }

    /** The host name to connect to */
    var mongoDbHost: String? = null
        private set

    /** The port to connect to */
    var mongoDbPort: Int? = null
        private set

    /** The name of the database to use */
    var databaseName: String? = null
        private set

    /** A Mongo Client to interact with */
    var mongoClient: MongoClient? = null
        private set

    /** Get the actual database in a safe manner */
    val mongoDatabase: MongoDatabase
        get() = mongoClient!!.getDatabase(databaseName)

    /**
     * Actually apply the rule and power the MongoDB database before the test, and tear it down afterward
     * @param base The base statement to wrap
     * @param description The description of this test
     * @return the wrapped statement
     */
    override fun apply(base: Statement, description: Description): Statement {
        val mongoDbConfig = description.getAnnotation(StartMongoDb::class.java)

        val result = if (mongoDbConfig == null) {
            base
        } else {
            object : Statement() {
                override fun evaluate() {
                    LOG.debug("Starting up MongoDB Database")

                    val starter = MongodStarter.getDefaultInstance()
                    val networkConfig = Net(Network.getFreeServerPort(),
                            Network.localhostIsIPv6())
                    val config = MongodConfigBuilder()
                            .version(Version.Main.V3_2)
                            .net(networkConfig)
                            .build()
                    val executable = starter.prepare(config)

                    try {
                        executable.start()
                        mongoDbPort = networkConfig.port
                        mongoDbHost = networkConfig.serverAddress.hostAddress
                        databaseName = UUID.randomUUID().toString()

                        mongoClient = MongoClient(networkConfig.serverAddress.hostAddress,
                                networkConfig.port)

                        mongoDbConfig.value.forEach { seed ->
                            val seedData = SeedDataLoader.loadSeedData(seed.source)
                            LOG.debug("Loading seed data {} into collection {}", seedData, seed.collection)
                            mongoDatabase.createCollection(seed.collection)
                            mongoDatabase.getCollection(seed.collection)
                                .insertMany(seedData.map { s -> Document(s) })
                        }
                        base.evaluate()
                    } finally {
                        executable.stop()
                        mongoDbHost = null
                        mongoDbPort = null
                        databaseName = null
                        mongoClient = null
                        LOG.debug("Shutting down MongoDB Database")
                    }
                }
            }
        }

        return result
    }
}