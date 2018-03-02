package edu.ycp.cs482.iorcapi.config

import com.mongodb.Mongo
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["edu.ycp.cs482.iorcapi.repositories"])
open class ApplicationConfig : AbstractMongoConfiguration(){

    @Value("\${spring.profiles.active}")
     private val profileActive: String? = null

//    @Value("\${spring.application.name}")
//    private val proAppName: String? = null

    @Value("\${spring.data.mongodb.uri}")
    private val mongoUri: String? = null


    override fun getDatabaseName(): String {
        return "iOrcDb"
    }

    override fun mongo(): Mongo {
        return MongoClient(MongoClientURI(mongoUri))
    }

    @Bean
    override fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongo(), databaseName)
    }
}