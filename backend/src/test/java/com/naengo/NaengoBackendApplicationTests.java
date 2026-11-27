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
 * 서버 전체를 MySQL(Testcontainers)에 붙여 띄워 본다. Flyway 마이그레이션과 엔티티 검증(validate)까지 여기서 걸린다.
 * compose healthcheck가 부르는 /actuator/health와, HTTP로 연 Actuator 엔드포인트가 그것 하나인지도 여기서 본다
 * (따로 두면 Spring 컨텍스트와 MySQL 컨테이너가 한 벌 더 뜬다).
 * Docker가 꺼져 있으면 건너뛴다(CI에서는 항상 돈다).
 */
@Testcontainers(disabledWithoutDocker = true)
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class NaengoBackendApplicationTests {

	@Autowired
	MockMvcTester mvc;

	@Test
	void contextLoads() {
	}

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
