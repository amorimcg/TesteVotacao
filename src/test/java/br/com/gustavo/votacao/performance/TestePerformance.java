package br.com.gustavo.votacao.performance;

import br.com.gustavo.votacao.Enum.Escolha;
import br.com.gustavo.votacao.Enum.Resposta;
import br.com.gustavo.votacao.VotacaoApplication;
import br.com.gustavo.votacao.entity.Associado;
import br.com.gustavo.votacao.entity.Pauta;
import br.com.gustavo.votacao.entity.Voto;
import br.com.gustavo.votacao.mensagem.RabbitMQSender;
import br.com.gustavo.votacao.response.StatusResposta;
import br.com.gustavo.votacao.service.VotacaoService;
import br.com.gustavo.votacao.util.Utilidades;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
public class TestePerformance extends VotacaoApplication {

    private final VotacaoService votacaoService = new VotacaoService();

    @Autowired
    private RabbitMQSender rabbitMqSender;

    @Test
    public void testePerformance() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(votacaoService.cadastraPauta(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        StepVerifier.create(Utilidades.criaSessao(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        AtomicInteger i = new AtomicInteger();
        Flux<StatusResposta> b = Mono.just(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .flatMap(a -> votacaoService.cadastraVoto(buildVoto(i.getAndIncrement(), buildPauta(idPauta)), rabbitMqSender))
                .filter(x -> x.getResposta() != Resposta.SUCESSO)
                .repeatWhen(z -> votacaoService.cadastraVoto(buildVoto(i.getAndIncrement(), buildPauta(idPauta)), rabbitMqSender))
                .repeat();
        System.out.println("Systema processou finalizou com status da pauta = " + b.blockFirst() );
    }

    private Pauta buildPauta(int idPauta){
        return Pauta.builder().id(idPauta).nomePauta("PautaTeste_".concat(String.valueOf(idPauta))).build();
    }

    private Voto buildVoto(int cpf, Pauta pauta) {
        return Voto.builder()
                .associado(buildAssociado(String.valueOf(cpf)))
                .pauta(pauta)
                .escolha(cpf % 2 == 0 ? Escolha.NAO : Escolha.SIM)
                .build();
    }

    private Associado buildAssociado(String cpf) {
        return Associado.builder().cpf(cpf).build();
    }
}
