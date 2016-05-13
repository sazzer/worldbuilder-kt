package uk.co.grahamcox.worldbuilder.verification

import org.slf4j.LoggerFactory
import java.lang.reflect.Field

/**
 * Mechanism by which we can populate objects from data table entries
 */
class ModelPopulator {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(ModelPopulator::class.java)
    }

    /**
     * Create and populate an object of the given type from the given data
     * @param data The data to populate the object with
     * @param target The target type to populate. Must have a default constructor
     * @return the created object
     */
    fun <T> populate(data: List<DataTableEntry>, target: Class<T>) : T {
        val result: T = target.newInstance()

        data.map { Pair(it.key, it.value) }
            .map { Pair(findField(target, it.first), it.second) }
            .filter { it.first != null }
            .forEach {
                it.first!!.isAccessible = true
                it.first!!.set(result, it.second)
            }

        LOG.debug("Parsed data table {} into result {}", data, result)
        return result
    }

    /**
     * Find the field that has the given name, but when the name isn't in the exact same form.
     * For now this strips out all whitespace from the name and then does a comparison ignoring case
     * @param target The class to find the field in
     * @param name The name to look for
     * @return the field if we found one. Null if not
     */
    private fun findField(target: Class<*>, name: String): Field? {
        val matchingName = name.replace(" ", "").toLowerCase()
        val result = target.declaredFields.filter { it.name.toLowerCase().equals(matchingName) }
                .firstOrNull()

        if (result == null) {
            LOG.warn("Failed to find a suitable field on {} for name {}", target, name)
        }

        return result
    }
}