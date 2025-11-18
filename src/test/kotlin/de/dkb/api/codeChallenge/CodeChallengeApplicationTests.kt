package de.dkb.api.codeChallenge

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class CodeChallengeApplicationTests {

	@Test
	fun contextLoads() {
		// Spring Boot will load the context using H2 in-memory DB
		// as configured in application.yml
	}
}