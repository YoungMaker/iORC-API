package edu.ycp.cs482.iorcapi.model.authentication

import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Scope
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader

@RunWith(SpringJUnit4ClassRunner::class)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class JtwUtilsTest {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Test
    fun createJwt() {
        System.out.println(jwtUtils.createJwt("ID"))
    }
}