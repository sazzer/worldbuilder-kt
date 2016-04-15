package uk.co.grahamcox.worldbuilder.webapp

import io.kotlintest.specs.FreeSpec
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Controller
@RequestMapping("/some/root")
class ControllerClass {
    @RequestMapping("/leaf")
    fun handler() {

    }

    @RequestMapping("/path/{id}/leaf")
    fun pathHandler(@PathVariable("id") id: String) {

    }

    @RequestMapping("/query")
    fun queryHandler(@RequestParam("q") q: String) {

    }

    @RequestMapping("/{id}")
    fun pathQueryHandler(@PathVariable("id") id: String, @RequestParam("q") q: String) {

    }

    fun unmapped() {

    }
}

/**
 * Unit tests for the URL Builder helper
 */
class UrlBuilderTest : FreeSpec() {
    init {
        "When building a URL to a controller method" - {
            "that has no arguments" with {
                ControllerClass::handler.routeTo() shouldBe "https://server.host:6789/some/root/leaf"
            }
            "That has a single Path parameter" with {
                ControllerClass::pathHandler.routeTo("hello") shouldBe "https://server.host:6789/some/root/path/hello/leaf"
            }
            "That has a single Query parameter" with {
                ControllerClass::queryHandler.routeTo("hello") shouldBe "https://server.host:6789/some/root/query?q=hello"
            }
            "That has both Path and Query parameters" with {
                ControllerClass::pathQueryHandler.routeTo("hello", "world") shouldBe "https://server.host:6789/some/root/hello?q=world"
            }
        }
        "When building a URL to a non-controller method" - {
            "should fail" with {
                expecting(IllegalArgumentException::class) {
                    ControllerClass::unmapped.routeTo()
                }
            }
        }
    }

    /**
     * Set up the "current" request details
     */
    override fun beforeAll() {
        val request = MockHttpServletRequest("GET", "/some/other")
        request.scheme = "https"
        request.serverName = "server.host"
        request.serverPort = 6789
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
    }

    /**
     * Reset the "current" request details
     */
    override fun afterAll() {
        RequestContextHolder.resetRequestAttributes()
    }
}