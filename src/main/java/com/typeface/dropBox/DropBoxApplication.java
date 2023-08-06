package com.typeface.dropBox;

import com.typeface.dropBox.domain.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class DropBoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(DropBoxApplication.class, args);
	}

}
