package ssi1.integrated;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class Ssi1ItbKradanKanbanApplication {
    public static void main(String[] args) {
        SpringApplication.run(Ssi1ItbKradanKanbanApplication.class, args);
    }

}
