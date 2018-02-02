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
        val character = characterRepository.findByName("Test Man")
        return character.toString()
    }
}