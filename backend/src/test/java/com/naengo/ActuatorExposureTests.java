package com.naengo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * compose healthcheck가 부르는 /actuator/health와, HTTP로 연 Actuator 엔드포인트가 그것 하나인지 본다.
 */
@Testcontainers(disabledWithoutDocker = true)
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class ActuatorExposureTests {

	@Autowired
	MockMvcTester mvc;

	@Test
	void 상태_확인_주소는_DB에_붙으면_UP을_돌려준다() {
		assertThat(mvc.get().uri("/actuator/health"))
				.hasStatusOk()
				.bodyJson().extractingPath("$.status").isEqualTo("UP");
	}

	@Test
	void HTTP로_연_Actuator_엔드포인트는_health뿐이다() {
		assertThat(mvc.get().uri("/actuator"))
				.hasStatusOk()
				.bodyJson().extractingPath("$._links").asMap().containsOnlyKeys("self", "health", "health-path");
	}

}
