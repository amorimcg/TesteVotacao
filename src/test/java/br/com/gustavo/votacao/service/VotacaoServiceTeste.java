package br.com.gustavo.votacao.service;

import br.com.gustavo.votacao.Enum.Escolha;
import br.com.gustavo.votacao.Enum.Resposta;
import br.com.gustavo.votacao.VotacaoApplication;
import br.com.gustavo.votacao.entity.Associado;
import br.com.gustavo.votacao.entity.Pauta;
import br.com.gustavo.votacao.entity.Voto;
import br.com.gustavo.votacao.mensagem.RabbitMQSender;
import br.com.gustavo.votacao.response.StatusResposta;
import br.com.gustavo.votacao.util.Utilidades;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.util.Date;

@RunWith(SpringRunner.class)
public class VotacaoServiceTeste extends VotacaoApplication  {

    private final VotacaoService votacaoService = new VotacaoService();

    @Autowired
    private RabbitMQSender rabbitMqSender;

    @Test
    public void testePauta() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(votacaoService.cadastraPauta(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();
    }

    @Test
    public void testeCriarSessao() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(Utilidades.criaSessao(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();
    }

    @Test
    public void testeSessaoAberta() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(Utilidades.criaSessao(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        StepVerifier.create(Utilidades.verificarSessaoAberta(buildPauta(idPauta), rabbitMqSender))
                .expectNext(StatusResposta.builder().resposta(Resposta.PAUTA_ABERTA).build())
                .expectComplete()
                .verify();
    }

    @Test
    public void testeFecharSessao() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(Utilidades.removerSessao(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();
    }

    @Test
    public void testeSessaoFechada() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(Utilidades.verificarSessaoAberta(buildPauta(idPauta), rabbitMqSender))
                .expectNext(StatusResposta.builder().resposta(Resposta.PAUTA_FECHADA).build())
                .expectComplete()
                .verify();
    }

    @Test
    public void testeVoto() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(votacaoService.cadastraPauta(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        StepVerifier.create(Utilidades.criaSessao(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        StepVerifier.create(votacaoService.cadastraVoto(buildVoto(buildPauta(idPauta)), rabbitMqSender))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();
    }

    @Test
    public void testeContabilizaVoto() {

        int idPauta = (int) new Date().getTime();
        StepVerifier.create(votacaoService.cadastraPauta(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        StepVerifier.create(Utilidades.criaSessao(buildPauta(idPauta)))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        StepVerifier.create(votacaoService.cadastraVoto(buildVoto(buildPauta(idPauta)), rabbitMqSender))
                .expectNext(StatusResposta.builder().resposta(Resposta.SUCESSO).build())
                .expectComplete()
                .verify();

        StepVerifier.create(votacaoService.contabilizaVotos(buildPauta(idPauta), rabbitMqSender))
                . expectNextMatches(resultado -> resultado.getSim() == 1 &&
                                                 resultado.getNao() == 0 &&
                                                 resultado.getNomePauta().equals("PautaTeste_".concat(String.valueOf(idPauta))) &&
                                                 resultado.getId() == idPauta)
                .verifyComplete();
    }

    private Pauta buildPauta(int idPauta){
        return Pauta.builder().id(idPauta).nomePauta("PautaTeste_".concat(String.valueOf(idPauta))).build();
    }

    private Voto buildVoto(Pauta pauta) {
        return Voto.builder()
                .associado(buildAssociado(String.valueOf(12345)))
                .pauta(pauta)
                .escolha(Escolha.SIM)
                .build();
    }

    private Associado buildAssociado(String cpf) {
        return Associado.builder().cpf(cpf).build();
    }
}
