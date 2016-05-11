package uk.co.grahamcox.worldbuilder.verification.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.ResourceHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient
import java.net.URI

/**
 * The root of the Cucumber Spring configuration
 */
@Configuration
@Import(
        HealthCheckConfig::class
)
open class CucumberConfig {

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

    /**
     * The Rest Template to use to communicate with the server
     */
    @Autowired
    @Bean
    open fun restTemplate(objectMapper: ObjectMapper): RestTemplate {
        val restTemplate = RestTemplate()
        // First, add all of the default converters we actually care about
        restTemplate.messageConverters.add(ByteArrayHttpMessageConverter())
        restTemplate.messageConverters.add(StringHttpMessageConverter())
        restTemplate.messageConverters.add(ResourceHttpMessageConverter())
        restTemplate.messageConverters.add(FormHttpMessageConverter())

        // Then our custom ones
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter(objectMapper))

        return restTemplate
    }

    /**
     * The GraphQL Client to use to communicate with the server
     */
    @Autowired
    @Bean
    open fun graphQlClient(restTemplate: RestTemplate): GraphQLClient {
        val url = URI(System.getProperty("url.worldbuilder") + "/api/graphql")
        return GraphQLClient(restTemplate, url)
    }
}