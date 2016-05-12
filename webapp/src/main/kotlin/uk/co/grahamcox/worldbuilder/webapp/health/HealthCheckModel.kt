package uk.co.grahamcox.worldbuilder.webapp.health

/**
 * Enumeration of the status of a given health check
 */
enum class HealthCheckStatus {
    /** The health check is ok */
    OK,
    /** The health check has a warning but the system should still work */
    WARNING,
    /** The health check indicates that the system is down */
    FATAL
}

/**
 * The actual data representing a single health check response
 * @property name The name of the health check
 * @property status The status of the health check
 * @property message A message from the health check
 */
data class HealthCheckModel(val name: String,
                            val status: HealthCheckStatus,
                            val message: String?)