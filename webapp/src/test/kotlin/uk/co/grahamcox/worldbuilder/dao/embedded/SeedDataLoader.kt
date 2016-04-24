package uk.co.grahamcox.worldbuilder.dao.embedded

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import uk.co.grahamcox.worldbuilder.dao.embedded.transformers.DateTimeTransformer
import java.io.FileNotFoundException



object SeedDataLoader {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(SeedDataLoader::class.java)

    /** The separator before the data type */
    private val DATA_TYPE_SEPARATOR = "::"

    private val DATA_TYPE_TRANSFORMERS = mapOf(
            "datetime" to DateTimeTransformer()
    )

    /** The Object Mapper to load the seed data with */
    private val objectMapper = ObjectMapper()

    /**
     * Actually load the Seed Data from the given classpath file
     * @param seedSource The seed source file to load
     * @return the loaded source
     */
    fun loadSeedData(seedSource: String) : List<Map<String, *>> {
        val resource = ClassPathResource(seedSource)
        if (resource.isReadable) {
            val loaded = objectMapper.readValue(resource.inputStream, List::class.java)
            val transformed = process(loaded)
            LOG.debug("Loaded {} as {}", seedSource, transformed)
            return transformed as List<Map<String, *>>
        } else {
            throw FileNotFoundException("Seed Data file not found: ${seedSource}")
        }
    }

    /**
     * Process a provided list of resources
     * @param input The input list to process
     * @return the processed list
     */
    private fun process(input: List<*>) : List<*> = input.map { v ->
        when (v) {
            is List<*> -> process(v)
            is Map<*, *> -> process(v)
            else -> v
        }
    }

    /**
     * Process an input map of resources.
     * If any of the entries in the map have keys ending in a special suffix then the value will be processed in some way
     * depending on the suffix
     * @param input The input map to process
     */
    private fun process(input: Map<*, *>) = input.map { e ->
        val key = e.key
        if (key is String && key.contains(DATA_TYPE_SEPARATOR)) {
            val (newKey, type) = key.split(DATA_TYPE_SEPARATOR)
            val transformer = DATA_TYPE_TRANSFORMERS[type]
            val newValue = if (transformer != null) {
                LOG.debug("Transforming value {} for key {} as type {}", e.value, newKey, type)
                transformer.transform(e.value)
            } else {
                LOG.warn("Not transforming value {} for key {} as unknown type {}", e.value, newKey, type)
                e.value
            }
            LOG.debug("Transformed {}={} into {}={}", e.key, e.value, newKey, newValue)
            newKey to newValue
        } else {
            key to e.value
        }
    }.toMap()
}