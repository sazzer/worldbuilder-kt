package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * Configuration for Spring WebMVC
 */
@Configuration
@EnableWebMvc
open class WebMvcConfig : WebMvcConfigurerAdapter() {
}