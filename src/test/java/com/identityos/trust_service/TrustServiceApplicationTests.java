package com.identityos.trust_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named="IDENTITY_OS_TEST_DB_URL",matches="jdbc:postgresql:.*")
class TrustServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
