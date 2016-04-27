package uk.co.grahamcox.worldbuilder.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.*
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import java.time.Clock

/**
 * Spring configuration for the core functionality
 */
@Configuration
@Import(
    DatabaseConfig::class,
    UsersConfig::class
)
@PropertySources(
        PropertySource("classpath:/config/database.properties")
)
open class CoreConfig {
    /**
     * Get the clock to use
     * @return the clock to use
     */
    @Bean
    open fun clock() = Clock.systemUTC()

    /**
     * Construct the Jackson Object Mapper to use
     * @return the Jackson Object Mapper
     */
    @Bean
    open fun objectMapper() : ObjectMapper {
        val objectMapper = ObjectMapper();
        objectMapper.registerModule(JavaTimeModule());
        objectMapper.registerModule(Jdk8Module());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        return objectMapper;
    }

    /** Property Configurer */
    @Bean
    open fun propertyConfig() = PropertySourcesPlaceholderConfigurer()
}