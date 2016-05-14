package uk.co.grahamcox.worldbuilder.verification

import org.apache.commons.jxpath.JXPathContext
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

/**
 * Mechanism by which we can populate objects from data table entries
 * @property paths Map of the JXPaths to use for populating the object
 */
class ModelPopulator(private val paths: Map<String, String>) {
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
        val jxpath = JXPathContext.newContext(result)

        data.filter {
                    if (!paths.containsKey(it.key)) {
                        LOG.warn("Unknown field {} for target type {}", it.key, target)
                    }
                    paths.containsKey(it.key)
                }
                .map { Pair(paths[it.key]!!, it.value) }
                .forEach {
                    LOG.debug("Populating path {} with value {}", it.first, it.second)
                    jxpath.setValue(it.first, it.second)
                }

        LOG.debug("Parsed data table {} into result {}", data, result)
        return result
    }

}