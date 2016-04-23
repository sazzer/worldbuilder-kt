package uk.co.grahamcox.worldbuilder.dao.embedded

/**
 * Annotation to indicate a Database collection to seed on database startup
 * @property collection The name of the collection to seed
 * @property source The file to seed the database from
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class SeedCollection(val collection: String, val source: String)