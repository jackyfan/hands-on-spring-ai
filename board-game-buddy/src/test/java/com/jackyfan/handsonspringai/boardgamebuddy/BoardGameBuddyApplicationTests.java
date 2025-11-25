package com.jackyfan.handsonspringai.boardgamebuddy;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(properties = "spring.ai.openai.base-url=${openai.base.url}")
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "openai.base.url"))
class BoardGameBuddyApplicationTests {

	@Test
	void contextLoads() {
	}

}
