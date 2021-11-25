package br.com.gustavo.votacao;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
public class VotacaoApplication {

    @Generated
    public static void main(String[] args) {
        SpringApplication.run(VotacaoApplication.class, args);
    }
}
