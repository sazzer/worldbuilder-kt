package uk.co.grahamcox.worldbuilder.webapp.swagger.annotations

/**
 * Annotation to indicate the details of a response
 * @property value The collection of responses
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class SwaggerResponses(val value: Array<SwaggerResponse>)