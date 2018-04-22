package edu.ycp.cs482.iorcapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication



//@EnableAutoConfiguration
@SpringBootApplication
open class IorcApiApplication : CommandLineRunner {
    @Autowired
    lateinit var systemInit: SystemInit

    override fun run(vararg args: String?) {
        systemInit.addTestRaces()
        systemInit.addTestClasses()
        systemInit.addTestCharacters()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(IorcApiApplication::class.java, *args)
        }
    }
}
