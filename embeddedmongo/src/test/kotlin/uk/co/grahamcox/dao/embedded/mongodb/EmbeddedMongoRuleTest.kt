package uk.co.grahamcox.dao.embedded.mongodb

import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Unit tests for the Embedded Mongo Rule
 */
class EmbeddedMongoRuleTest {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(EmbeddedMongoRuleTest::class.java)
    }

    /** The actual rule */
    @org.junit.Rule
    @JvmField
    val embeddedMongoRule = EmbeddedMongoRule()

    /**
     * Test that the rule sets the properties needed to connect
     */
    @org.junit.Test
    @StartMongoDb
    fun testMongoDbProperties() {
        LOG.debug("MongoDB is running on {}:{}", embeddedMongoRule.mongoDbHost, embeddedMongoRule.mongoDbPort)
        LOG.debug("Database name is {}", embeddedMongoRule.databaseName)
        org.junit.Assert.assertNotNull(embeddedMongoRule.mongoDbHost)
        org.junit.Assert.assertNotNull(embeddedMongoRule.mongoDbPort)
        org.junit.Assert.assertNotNull(embeddedMongoRule.databaseName)
        org.junit.Assert.assertNotNull(embeddedMongoRule.mongoClient)
    }

    /**
     * Test that the rule starts up a database
     */
    @org.junit.Test
    @StartMongoDb
    fun testMongoDbStarted() {
        LOG.debug("MongoDB is running on {}:{}", embeddedMongoRule.mongoDbHost, embeddedMongoRule.mongoDbPort)
        val client = MongoClient(embeddedMongoRule.mongoDbHost, embeddedMongoRule.mongoDbPort!!)
        LOG.debug("Database names: {}", client.listDatabaseNames().toList())
    }

    /**
     * Test that the rule seeds the database correctly
     */
    @org.junit.Test
    @StartMongoDb
    fun testProvidedClient() {
        LOG.debug("MongoDB is running on {}:{}", embeddedMongoRule.mongoDbHost, embeddedMongoRule.mongoDbPort)
        LOG.debug("Database names: {}", embeddedMongoRule.mongoClient!!.listDatabaseNames().toList())
    }

    /**
     * Test that the rule starts up a database
     */
    @org.junit.Test
    @StartMongoDb(arrayOf(
            SeedCollection(collection = "firstCollection", source = "/uk/co/grahamcox/dao/embedded/mongodb/first.json")
    ))
    fun testSeed() {
        LOG.debug("MongoDB is running on {}:{}", embeddedMongoRule.mongoDbHost, embeddedMongoRule.mongoDbPort)
        LOG.debug("Database names: {}", embeddedMongoRule.mongoClient!!.listDatabaseNames().toList())

        val db = embeddedMongoRule.mongoDatabase
        org.junit.Assert.assertTrue(db.listCollectionNames().toList().contains("firstCollection"))
        org.junit.Assert.assertEquals(2, db.getCollection("firstCollection").count())
        org.junit.Assert.assertEquals(1, db.getCollection("firstCollection").count(BasicDBObject().append("hello", "world")))
        org.junit.Assert.assertEquals(1, db.getCollection("firstCollection").count(BasicDBObject().append("answer", 42)))

    }
}