package com.tester.classicmodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@SpringBootApplication
@RestController
public class ClassicmodelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClassicmodelApplication.class, args);
	}

	@GetMapping("/")
	public String home(){
		try{
			String hostName = InetAddress.getLocalHost().getHostName();
			return "Hello from springboot! pod: " + hostName;
		}catch(Exception e) {
			return "Hello from springboot!!!";
		}
	}

	@GetMapping("/health")
	public String health(){
		return "OK";
	}


}
