package com.naengo;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * "오늘"과 현재 시각은 이 시계에서만 얻는다. 서버 시간대(UTC)와 상관없이 한국 날짜로 판단한다(유통기한 계산 등).
 * 테스트에서 날짜를 고정할 때는 @TestBean으로 Clock.fixed(...)를 넣는다.
 * (@MockitoBean으로 만든 가짜 시계는 getZone()이 null이라 LocalDate.now(clock)이 실패한다.)
 */
@Configuration(proxyBeanMethods = false)
public class ClockConfig {

	@Bean
	Clock clock() {
		return Clock.system(ZoneId.of("Asia/Seoul"));
	}

}
