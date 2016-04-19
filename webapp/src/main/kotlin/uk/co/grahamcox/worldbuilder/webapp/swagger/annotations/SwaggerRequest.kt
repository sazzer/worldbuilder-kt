package uk.co.grahamcox.worldbuilder.webapp.swagger.annotations

import org.springframework.http.HttpStatus

/**
 * Annotation to indicate the details of a request payload
 * @property description The description of the request
 * @property schema The schema describing the request
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SwaggerRequest(val description: String,
                                val schema: String)