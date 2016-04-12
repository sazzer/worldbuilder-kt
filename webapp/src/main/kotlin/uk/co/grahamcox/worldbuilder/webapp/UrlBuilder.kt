package uk.co.grahamcox.worldbuilder.webapp

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

/**
 * Build the URL to the controller method
 * @param args The arguments to the controller method
 * @return the URL
 */
fun KFunction<*>.routeTo(vararg args: Any): String {
    return MvcUriComponentsBuilder.fromMethod(this.javaMethod?.declaringClass, this.javaMethod, *args)
            .build()
            .toUriString()
}