package uk.co.grahamcox.worldbuilder.webapp.swagger

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.AbstractFactoryBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.*
import uk.co.grahamcox.worldbuilder.webapp.swagger.model.*
import java.lang.reflect.Method

/**
 * Mechanism to build the Swagger Schema to use
 */
class SwaggerSchemaBuilder : AbstractFactoryBean<Schema>(), ApplicationContextAware {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(SwaggerSchemaBuilder::class.java)

    /** The application context to use */
    private lateinit var applicationContext: ApplicationContext
    /**
     * Get the type of object that we are going to build
     * @return Schema.class
     */
    override fun getObjectType() = Schema::class.java

    /**
     * Actually build the Swagger Schema
     * @return the build schema
     */
    override fun createInstance(): Schema {
        val controllers = applicationContext.getBeansWithAnnotation(Controller::class.java)
        val handlerMappings = controllers
                .values
                .flatMap { controller -> controller.javaClass.methods.toList() }
                .filter { method -> method.isAnnotationPresent(RequestMapping::class.java) }
                .groupBy(::buildUrl)
                .mapValues { handlers -> handlers.value.groupBy(::determineHttpMethods) }
                .mapValues { handlers -> flattenHandlerMap(handlers.key, handlers.value) }

        val paths = handlerMappings.mapValues { handlers -> buildPath(handlers.value) }

        return Schema(
                info = Info(
                        title = "Worldbuilder",
                        version = "1.0"
                ),
                paths = paths)
    }

    /**
     * Inject the Application Context from which we can get the controllers
     * @param context The application context
     */
    override fun setApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }
}

/**
 * Build the URL for the given Handler Method
 * @param handlerMethod The handler method
 * @return the URL
 */
private fun buildUrl(handlerMethod: Method): String {
    val controller = handlerMethod.declaringClass
    val controllerRequestMapping = controller.getAnnotation(RequestMapping::class.java)
    val controllerUrl = controllerRequestMapping?.value?.firstOrNull() ?: ""

    val handlerRequestMapping = handlerMethod.getAnnotation(RequestMapping::class.java)
    val handlerUrl = handlerRequestMapping?.value?.firstOrNull() ?: ""

    val joiner = if (handlerUrl.isBlank() || handlerUrl.startsWith("/")) {
        ""
    } else {
        "/"
    }

    return controllerUrl + joiner + handlerUrl
}

/**
 * Determine the HTTP Methods to use for the given Handler Method
 * @param handlerMethod The handler method
 * @return the HTTP Method
 */
private fun determineHttpMethods(handlerMethod: Method): List<RequestMethod> {
    val controller = handlerMethod.declaringClass
    val controllerRequestMapping = controller.getAnnotation(RequestMapping::class.java)
    val controllerHttpMethods = controllerRequestMapping?.method?.toList()

    val handlerRequestMapping = handlerMethod.getAnnotation(RequestMapping::class.java)
    val handlerHttpMethods = handlerRequestMapping?.method?.toList()

    return if (handlerHttpMethods != null && handlerHttpMethods.isNotEmpty()) {
        handlerHttpMethods
    } else if (controllerHttpMethods != null && controllerHttpMethods.isNotEmpty()) {
        controllerHttpMethods
    } else {
        listOf(RequestMethod.GET)
    }
}

/**
 * Flatten a map of List<RequestMethod> to List<Method> into a Map of RequestMethod to Method.
 * This requires the list of Methods to be exactly one long, which it always will be, and that the Request Methods are never duplicated
 * @param path The path that we are mapping. Used purely for diagnostic purposes
 * @param handlerMap The handler map to flatten
 * @return the flattened map
 */
private fun flattenHandlerMap(path: String,
                              handlerMap: Map<List<RequestMethod>, List<Method>>): Map<RequestMethod, Method> {
    val result = mutableMapOf<RequestMethod, Method>()

    handlerMap.forEach { handler ->
        if (handler.value.size != 1) {
            throw IllegalArgumentException("Found multiple handler methods for ${handler.key} ${path}")
        }
        val handlerMethod = handler.value[0]

        handler.key.forEach { httpMethod ->
            if (result.containsKey(httpMethod)) {
                throw IllegalArgumentException("Found two mappings for ${httpMethod} ${path}")
            }
            result.put(httpMethod, handlerMethod)
        }
    }

    return result
}

/**
 * Build the Swagger Path object for the given handlers
 * @param handlersMap The handlers map for a single URL Path
 * @return the Swagger Paths
 */
private fun buildPath(handlersMap: Map<RequestMethod, Method>) = Path(
        get = buildOperation(handlersMap[RequestMethod.GET]),
        put = buildOperation(handlersMap[RequestMethod.PUT]),
        post = buildOperation(handlersMap[RequestMethod.POST]),
        delete = buildOperation(handlersMap[RequestMethod.DELETE]),
        patch = buildOperation(handlersMap[RequestMethod.PATCH]),
        head = buildOperation(handlersMap[RequestMethod.HEAD]),
        options = buildOperation(handlersMap[RequestMethod.OPTIONS])
)

/**
 * Build a Swagger Operation for a given handler method
 * @param handlerMethod The handler method to work with
 * @return the Swagger Operation
 */
private fun buildOperation(handlerMethod: Method?) = when(handlerMethod) {
    null -> null
    else -> {
        val swaggerSummary = handlerMethod.getAnnotation(SwaggerSummary::class.java)?.value
        val handlerSwaggerTags = handlerMethod.getAnnotation(SwaggerTags::class.java)?.value?.toSet() ?: setOf<String>()
        val controllerSwaggerTags = handlerMethod.declaringClass.getAnnotation(SwaggerTags::class.java)?.value?.toSet() ?: setOf<String>()
        val allTags = handlerSwaggerTags + controllerSwaggerTags

        val pathParams = handlerMethod.parameters
            .filter { param -> param.isAnnotationPresent(PathVariable::class.java) }
            .map { param ->
                val pathVariable = param.getAnnotation(PathVariable::class.java)
                val variableName = if (pathVariable.value.isEmpty()) {
                    param.name
                } else {
                    pathVariable.value
                }

                val swaggerSummary = param.getAnnotation(SwaggerSummary::class.java)?.value

                Parameter(
                        name = variableName,
                        description = swaggerSummary ?: "Undocumented",
                        location = ParameterLocation.PATH,
                        type = DataType.STRING,
                        required = true
                )
            }

        val bodyParams = if (handlerMethod.isAnnotationPresent(SwaggerRequest::class.java)) {
            val swaggerRequest = handlerMethod.getAnnotation(SwaggerRequest::class.java)
            listOf(Parameter(
                name = "",
                    description = swaggerRequest.description,
                    location = ParameterLocation.BODY,
                    required = true,
                    schemaLocation = "/api/docs/schemas/${swaggerRequest.schema}"
            ))
        } else {
            listOf()
        }

        val allParams = pathParams + bodyParams

        val responseAnnotations = handlerMethod.getAnnotationsByType(SwaggerResponse::class.java).toList() +
                (handlerMethod.getAnnotation(SwaggerResponses::class.java)?.value ?: arrayOf())
        val responses = responseAnnotations.groupBy { response -> response.statusCode }
                .mapKeys { response -> response.key.value().toString() }
                .mapValues { response -> response.value.first() }
                .mapValues { response ->
                    val schemaLocation = when (response.value.schema) {
                        "" -> null
                        else -> "/api/docs/schemas/${response.value.schema}"
                    }
                    Response(response.value.description, schemaLocation)
                }

        Operation(
                tags = allTags.toTypedArray(),
                summary = swaggerSummary ?: "Undocumented",
                parameters = allParams.toTypedArray(),
                responses = responses
        )
    }
}
