package org.example.secureplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("org.example.secureplatform.mapper")
public class SecurePlatformApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(SecurePlatformApplication.class);
	}

}
