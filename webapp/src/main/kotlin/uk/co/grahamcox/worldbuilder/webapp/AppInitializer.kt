package uk.co.grahamcox.worldbuilder.webapp

import org.slf4j.LoggerFactory
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import uk.co.grahamcox.worldbuilder.spring.CoreConfig
import uk.co.grahamcox.worldbuilder.webapp.spring.WebappConfig
import javax.servlet.ServletContext

/**
 * Application Initializer for the webapp
 */
open class AppInitializer : WebApplicationInitializer {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(AppInitializer::class.java);

    /**
     * Handle all of the Startup functionality of the webapp
     * @param servletContext The Servlet Context to configure
     */
    override fun onStartup(servletContext : ServletContext) {
        LOG.info("Configuring the application")

        val coreContext = AnnotationConfigWebApplicationContext()
        coreContext.register(CoreConfig::class.java)
        coreContext.refresh()

        val webappContext = AnnotationConfigWebApplicationContext()
        webappContext.register(WebappConfig::class.java)
        webappContext.parent = coreContext

        val servlet = servletContext.addServlet("dispatcher", DispatcherServlet(webappContext))
        servlet.addMapping("/")
        servlet.setLoadOnStartup(1)
    }
}