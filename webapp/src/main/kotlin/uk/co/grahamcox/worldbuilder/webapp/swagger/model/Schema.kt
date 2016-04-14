package uk.co.grahamcox.worldbuilder.webapp.swagger.model

/**
 * The actual Swagger Schema
 * @property info The API level Informatin
 * @property paths The paths 
 */
data class Schema(val info: Info,
                  val paths: Map<String, Path>) {
    val swagger = "2.0"
}