package edu.ycp.cs482.iorcapi

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IorcApiApplicationTests {

	@Autowired
	lateinit var restTemplate: TestRestTemplate

	//TODO: dis test is broke
	@Test
	fun landingPageLoads() {
		val respBody = restTemplate.getForObject("/", String::class.java)
		assertThat(respBody)
				.isEqualTo("this is the landing page of the iOrc-API. Proceed to /graphiql for active content")
	}

}
