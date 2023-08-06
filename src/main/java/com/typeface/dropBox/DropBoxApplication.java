package com.typeface.dropBox;

import com.typeface.dropBox.domain.FileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorage.class
})
public class DropBoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(DropBoxApplication.class, args);
	}

}
