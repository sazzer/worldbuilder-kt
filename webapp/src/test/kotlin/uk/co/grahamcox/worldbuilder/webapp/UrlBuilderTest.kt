package uk.co.grahamcox.worldbuilder.webapp

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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
class UrlBuilderTest {
    /**
     * Set up the "current" request details
     */
    @Before
    fun setup() {
        val request = MockHttpServletRequest("GET", "/some/other")
        request.scheme = "https"
        request.serverName = "server.host"
        request.serverPort = 6789
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
    }

    /**
     * Reset the "current" request details
     */
    @After
    fun reset() {
        RequestContextHolder.resetRequestAttributes()
    }

    /**
     * Test writing a URL to a controller with no arguments
     */
    @Test
    fun testNoArgs() {
        Assert.assertEquals("https://server.host:6789/some/root/leaf", ControllerClass::handler.routeTo())
    }

    /**
     * Test writing a URL to a controller with a Path argument
     */
    @Test
    fun testPathArgs() {
        Assert.assertEquals("https://server.host:6789/some/root/path/hello/leaf", ControllerClass::pathHandler.routeTo("hello"))
    }

    /**
     * Test writing a URL to a controller with a Query argument
     */
    @Test
    fun testQueryArgs() {
        Assert.assertEquals("https://server.host:6789/some/root/query?q=hello", ControllerClass::queryHandler.routeTo("hello"))
    }

    /**
     * Test writing a URL to a controller with a Path and Query argument
     */
    @Test
    fun testPathAndQueryArgs() {
        Assert.assertEquals("https://server.host:6789/some/root/hello?q=world",
                ControllerClass::pathQueryHandler.routeTo("hello", "world"))
    }

    /**
     * Test writing a URL to a handler that isn't mapped to a URL
     */
    @Test(expected = IllegalArgumentException::class)
    fun testUnmapped() {
        ControllerClass::unmapped.routeTo()
    }
}