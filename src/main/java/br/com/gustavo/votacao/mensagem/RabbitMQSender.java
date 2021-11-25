package br.com.gustavo.votacao.mensagem;

import br.com.gustavo.votacao.entity.Resultado;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class RabbitMQSender {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routingkey}")
    private String routingkey;

    public Mono<Resultado> send(Mono<Resultado> resultado){

        if(null != resultado) {
            rabbitTemplate.convertAndSend(exchange, routingkey, resultado.toString());
        }
        return resultado;
    }
}
