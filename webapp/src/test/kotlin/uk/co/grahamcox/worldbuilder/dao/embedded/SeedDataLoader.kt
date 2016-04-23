package uk.co.grahamcox.worldbuilder.dao.embedded

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import java.io.FileNotFoundException

object SeedDataLoader {
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
            return objectMapper.readValue(resource.inputStream, List::class.java) as List<Map<String, *>>
        } else {
            throw FileNotFoundException("Seed Data file not found: ${seedSource}")
        }
    }
}