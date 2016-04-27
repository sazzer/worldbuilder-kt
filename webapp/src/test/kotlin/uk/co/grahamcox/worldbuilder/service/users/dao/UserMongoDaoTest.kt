package uk.co.grahamcox.worldbuilder.service.users.dao

import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.grahamcox.worldbuilder.dao.embedded.EmbeddedMongoRule
import uk.co.grahamcox.worldbuilder.dao.embedded.SeedCollection
import uk.co.grahamcox.worldbuilder.dao.embedded.StartMongoDb
import uk.co.grahamcox.worldbuilder.service.Identity
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.StaleResourceException
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Unit tests for the User Mongo DAO
 */
class UserMongoDaoTest {
    companion object {
        const val USERS_COLLECTION = "users"
    }

    /** The Embedded MongoDB */
    @Rule
    @JvmField
    val embeddedMongoDb = EmbeddedMongoRule()

    /** The test subject */
    lateinit var testSubject: UserDao

    /**
     * Set up the test subject
     */
    @Before
    fun setup() {
        testSubject = UserMongoDao(embeddedMongoDb.mongoDatabase.getCollection(USERS_COLLECTION),
                Clock.systemUTC())
    }

    /**
     * Test loading a user with an ID that doesn't exist
     */
    @Test(expected = ResourceNotFoundException::class)
    @StartMongoDb
    fun testGetUnknownUserById() {
        testSubject.getById(UserId("abcdef"))
    }

    /**
     * Test loading a user with an ID that does exist
     */
    @Test
    @StartMongoDb(arrayOf(
            SeedCollection(collection = USERS_COLLECTION, source = "/uk/co/grahamcox/worldbuilder/service/users/dao/users.json")
    ))
    fun testGetKnownUserById() {
        val user = testSubject.getById(UserId("abcdef"))
        Assert.assertNotNull(user.identity)
        user.identity?.apply {
            Assert.assertEquals(UserId("abcdef"), id)
            Assert.assertEquals(ZonedDateTime.of(2014, 4, 25, 12, 29, 0, 0, ZoneId.of("UTC")).toInstant(), created)
            Assert.assertEquals(ZonedDateTime.of(2016, 4, 25, 12, 29, 0, 0, ZoneId.of("UTC")).toInstant(), updated)
        }

        Assert.assertEquals("Graham", user.name)
        Assert.assertEquals("graham@grahamcox.co.uk", user.email)
        Assert.assertEquals(true, user.enabled)
        Assert.assertNull(user.verificationCode)
    }

    /**
     * Test deleting a user with an ID that does exist
     */
    @Test
    @StartMongoDb(arrayOf(
            SeedCollection(collection = USERS_COLLECTION, source = "/uk/co/grahamcox/worldbuilder/service/users/dao/users.json")
    ))
    fun testDeleteUser() {
        val user = testSubject.getById(UserId("abcdef"))
        testSubject.delete(user.identity!!)

        try {
            testSubject.getById(UserId("abcder"))
            Assert.fail("User should no longer exist: abcdef")
        } catch (e: ResourceNotFoundException) {
            // Expected
        }
    }

    /**
     * Test loading a user with an ID that doesn't exist
     */
    @Test(expected = ResourceNotFoundException::class)
    @StartMongoDb
    fun testDeleteUnknownUser() {
        testSubject.delete(Identity(
                id = UserId("abcdef"),
                created = Instant.now(),
                updated = Instant.now()
        ))
    }

    /**
     * Test loading a user that has been modified since it was last loaded
     */
    @Test(expected = StaleResourceException::class)
    @StartMongoDb(arrayOf(
            SeedCollection(collection = USERS_COLLECTION, source = "/uk/co/grahamcox/worldbuilder/service/users/dao/users.json")
    ))
    fun testDeleteStaleUser() {
        testSubject.delete(Identity(
                id = UserId("abcdef"),
                created = Instant.now(),
                updated = Instant.now()
        ))
    }

    /**
     * Test creating a new user
     */
    @Test
    @StartMongoDb
    fun testCreateUser() {
        val saved = testSubject.save(User(
                identity = null,
                name = "New User",
                email = "new@user.com",
                enabled = true,
                verificationCode = "abcdef"
        ))

        Assert.assertNotNull(saved.identity)

        val loaded = testSubject.getById(saved.identity!!.id)
        Assert.assertEquals("New User", loaded.name)
        Assert.assertEquals("new@user.com", loaded.email)
    }

    /**
     * Test updating an existing user
     */
    @Test
    @StartMongoDb(arrayOf(
            SeedCollection(collection = USERS_COLLECTION, source = "/uk/co/grahamcox/worldbuilder/service/users/dao/users.json")
    ))
    fun testUpdateUser() {
        val existing = testSubject.getById(UserId("abcdef"))

        val saved = testSubject.save(User(
                identity = existing.identity,
                name = "New User",
                email = "new@user.com",
                enabled = true,
                verificationCode = "abcdef"
        ))

        Assert.assertNotNull(saved.identity)

        val loaded = testSubject.getById(saved.identity!!.id)
        Assert.assertEquals("New User", loaded.name)
        Assert.assertEquals("new@user.com", loaded.email)
        Assert.assertEquals(ZonedDateTime.parse("2014-04-25T12:29:00Z").toInstant(), loaded.identity?.created)
        Assert.assertNotEquals(ZonedDateTime.parse("2016-04-25T12:29:00Z").toInstant(), loaded.identity?.updated)
    }

    /**
     * Test updating an existing user with an invalid updated date
     */
    @Test(expected = StaleResourceException::class)
    @StartMongoDb(arrayOf(
            SeedCollection(collection = USERS_COLLECTION, source = "/uk/co/grahamcox/worldbuilder/service/users/dao/users.json")
    ))
    fun testUpdateStaleUser() {
        testSubject.save(User(
                identity = Identity(
                        id = UserId("abcdef"),
                        created = Instant.now(),
                        updated = Instant.now()
                ),
                name = "New User",
                email = "new@user.com",
                enabled = true,
                verificationCode = "abcdef"
        ))
    }

    /**
     * Test updating an existing user with an invalid updated date
     */
    @Test(expected = ResourceNotFoundException::class)
    @StartMongoDb(arrayOf(
            SeedCollection(collection = USERS_COLLECTION, source = "/uk/co/grahamcox/worldbuilder/service/users/dao/users.json")
    ))
    fun testUpdateUnknownUser() {
        testSubject.save(User(
                identity = Identity(
                        id = UserId("unknown"),
                        created = Instant.now(),
                        updated = Instant.now()
                ),
                name = "New User",
                email = "new@user.com",
                enabled = true,
                verificationCode = "abcdef"
        ))
    }
}