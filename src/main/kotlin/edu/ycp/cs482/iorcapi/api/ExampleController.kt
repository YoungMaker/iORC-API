package edu.ycp.cs482.iorcapi.api

import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ExampleController {
    @Autowired
    lateinit var characterRepository: CharacterRepository

    @RequestMapping("/")
    @ResponseBody
    fun index(): String {
        return "this is the landing page of the iOrc-API. Proceed to /graphiql for active content"
    }
}