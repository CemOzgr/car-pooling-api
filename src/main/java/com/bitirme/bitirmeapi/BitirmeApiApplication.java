package com.bitirme.bitirmeapi;

import com.bitirme.bitirmeapi.file.FileService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class BitirmeApiApplication implements CommandLineRunner {

	@Resource
	private FileService fileService;

	public static void main(String[] args) {
		SpringApplication.run(BitirmeApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fileService.init();
	}
}
