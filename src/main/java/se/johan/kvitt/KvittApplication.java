package se.johan.kvitt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KvittApplication {

	public static void main(String[] args) {
		SpringApplication.run(KvittApplication.class, args);
        Logger log = LoggerFactory.getLogger(KvittApplication.class);
        log.info("TEST — detta ska tvinga fram fil-loggning");
	}

}
