package uk.co.grahamcox.worldbuilder.webapp.swagger.annotations

import org.springframework.http.HttpStatus

/**
 * Annotation to indicate the details of a response
 * @property statusCode The status code the response is for
 * @property description The description of the response
 * @property schema The schema describing the response
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SwaggerResponse(val statusCode: HttpStatus,
                                 val description: String,
                                 val schema: String = "")