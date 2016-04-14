package uk.co.grahamcox.worldbuilder.webapp.swagger

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import uk.co.grahamcox.worldbuilder.webapp.swagger.model.*

/**
 * Controller to serve up the Swagger documentation
 */
@Controller
@RequestMapping("/api/docs/swagger")
open class SwaggerController {

    /**
     * Get the Swagger schema
     * @return the schema
     */
    @RequestMapping
    @ResponseBody
    fun getSwaggerDocs() = Schema(
            info = Info(
                    title = "Worldbuilder",
                    version = "1.0"
            ),
            paths = mapOf(
                    "/api/worlds" to Path(
                            get = Operation(
                                    tags = arrayOf("world"),
                                    summary = "Get all of the worlds",
                                    responses = mapOf()
                            ),
                            post = Operation(tags = arrayOf("world"), responses = mapOf())
                    ),
                    "/api/worlds/{id}" to Path(
                            get = Operation(
                                    tags = arrayOf("world"),
                                    responses = mapOf(),
                                    summary = "Get a single World by ID",
                                    parameters = arrayOf(
                                            Parameter(
                                                    name = "id",
                                                    location = "path",
                                                    type = "string",
                                                    description = "The ID of the World",
                                                    required = true
                                            )
                                    )),
                            put = Operation(tags = arrayOf("world"), responses = mapOf()),
                            patch = Operation(tags = arrayOf("world"), responses = mapOf()),
                            delete = Operation(tags = arrayOf("world"), responses = mapOf())
                    )
            )
    )
}