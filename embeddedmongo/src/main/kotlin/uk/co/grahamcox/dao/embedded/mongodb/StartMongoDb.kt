package uk.co.grahamcox.dao.embedded.mongodb

/**
 * Annotation to indicate that a MongoDB database should be started for the test
 * @property value The collections to seed
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class StartMongoDb(val value: Array<SeedCollection> = arrayOf())