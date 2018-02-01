package edu.ycp.cs482.iorcapi

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class IorcApiApplication

fun main(args: Array<String>) {
    SpringApplication.run(IorcApiApplication::class.java, *args)
}
