package br.com.gustavo.votacao.job;

import br.com.gustavo.votacao.mensagem.RabbitMQSender;
import br.com.gustavo.votacao.util.Utilidades;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static br.com.gustavo.votacao.constantes.Constantes.CONST_PAUTA_DIR;

@Data
@Component
@Slf4j
public class MonitoraSessoes {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final RabbitMQSender rabbitMqSender;

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.debug("Verificando fechamento de sessao as {}", dateFormat.format(new Date()));
        try {
            Objects.requireNonNull(Utilidades.retornaPautas(CONST_PAUTA_DIR))
                    .forEach(pauta -> Utilidades.verificarSessaoAberta(pauta, rabbitMqSender));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
