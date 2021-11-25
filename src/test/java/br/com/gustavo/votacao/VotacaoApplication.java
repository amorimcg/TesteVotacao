package br.com.gustavo.votacao;

import br.com.gustavo.votacao.mensagem.RabbitMQSender;
import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@EnableAutoConfiguration
@ContextConfiguration(classes = RabbitMQSender.class)
public class VotacaoApplication {

    @Generated
    public static void main(String[] args) {
        SpringApplication.run(VotacaoApplication.class, args);
    }
}
