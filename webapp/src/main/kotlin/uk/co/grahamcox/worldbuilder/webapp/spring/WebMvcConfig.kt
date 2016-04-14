package uk.co.grahamcox.worldbuilder.webapp.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.resource.GzipResourceResolver
import org.springframework.web.servlet.resource.PathResourceResolver

/**
 * Configuration for Spring WebMVC
 */
@Configuration
@EnableWebMvc
open class WebMvcConfig : WebMvcConfigurerAdapter() {

    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(WebMvcConfig::class.java)

    /** The Object Mapper to use */
    @Autowired
    lateinit var objectMapper : ObjectMapper

    /**
     * Configure all of the message converters to use. Specifically we have a custom Jackson converter for JSON
     * @param converters The list of converters to work with
     */
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        LOG.info("Configuring the Message Converters to use")
        // First, add all of the default converters we actually care about
        converters.add(ByteArrayHttpMessageConverter())
        converters.add(StringHttpMessageConverter())
        converters.add(ResourceHttpMessageConverter())
        converters.add(FormHttpMessageConverter())

        // Then our custom ones
        converters.add(MappingJackson2HttpMessageConverter(objectMapper))
    }

    /**
     * Add any resource handlers for raw resources
     * @param registry The registry to add the handlers to
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/api/docs/swagger-ui/**")
                .addResourceLocations("classpath:/swagger-ui/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(GzipResourceResolver())
                .addResolver(PathResourceResolver())
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addRedirectViewController("/api/docs/swagger-ui", "/api/docs/swagger-ui/index.html")
        registry.addRedirectViewController("/api/docs/swagger-ui/", "/api/docs/swagger-ui/index.html")
    }
}