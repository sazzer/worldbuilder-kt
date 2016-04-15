package uk.co.grahamcox.worldbuilder.webapp.swagger

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerSummary
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerTags
import uk.co.grahamcox.worldbuilder.webapp.swagger.model.*

/**
 * Controller to serve up the Swagger documentation
 * @property schema The swagger schema to serve
 */
@Controller
@RequestMapping("/api/docs/swagger")
open class SwaggerController : BeanFactoryAware {
    /** The Spring Bean Factory from which to get the Swagger Schema */
    private lateinit var springBeanFactory: BeanFactory

    /**
     * Handler to load the schema
     */
    @SwaggerSummary("Generate the Swagger documentation")
    @SwaggerTags(arrayOf("swagger", "documentation"))
    @RequestMapping
    @ResponseBody
    fun getSchema(): Schema {
        return springBeanFactory.getBean(Schema::class.java)
    }

    /**
     * Set the Bean Factory, from which we can load the schema
     * @param beanFactory The bean factory
     */
    override fun setBeanFactory(beanFactory: BeanFactory) {
        springBeanFactory = beanFactory
    }
}
