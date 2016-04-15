package uk.co.grahamcox.worldbuilder.webapp.swagger.annotations

/**
 * Annotation providing the summary of a handler method
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SwaggerSummary(val value: String)