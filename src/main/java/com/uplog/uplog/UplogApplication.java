package com.uplog.uplog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //Jpa Auditing을 활성화시키는 annotation(spring data jpa에서 audit은 시간을 자동으로 넣어주는 기능)
@SpringBootApplication
public class UplogApplication {

	public static void main(String[] args) {
		SpringApplication.run(UplogApplication.class, args);
	}

}
