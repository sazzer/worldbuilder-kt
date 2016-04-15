package uk.co.grahamcox.worldbuilder.webapp.swagger.annotations

/**
 * Annotation for providing Swagger details of a handler method
 * @property value The summary text
 * @property tags The handler tags
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Swagger(val value: String,
                         val tags: Array<String> = arrayOf())