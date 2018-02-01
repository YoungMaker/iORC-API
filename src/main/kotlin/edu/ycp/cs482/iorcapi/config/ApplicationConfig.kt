package edu.ycp.cs482.iorcapi.config

import com.mongodb.Mongo
import com.mongodb.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["edu.ycp.cs482.iorcapi.repositories"])
open class ApplicationConfig : AbstractMongoConfiguration(){
    override fun getDatabaseName(): String {
        return "test"
    }

    override fun mongo(): Mongo {
        return MongoClient("localhost")
    }

    @Bean
    override fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongo(), databaseName)
    }
}