package com.image.processing;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com")
public class ImageProcessingApplication extends SpringBootServletInitializer {
	
	public static void main(String[] args) throws IOException {
		SpringApplication.run(ImageProcessingApplication.class, args);
		
	}


}
