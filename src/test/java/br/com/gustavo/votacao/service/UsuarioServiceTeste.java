package br.com.gustavo.votacao.service;

import br.com.gustavo.votacao.response.StatusResposta;
import org.junit.Test;
import reactor.test.StepVerifier;

import static br.com.gustavo.votacao.Enum.Resposta.ABLE_TO_VOTE;
import static br.com.gustavo.votacao.Enum.Resposta.UNABLE_TO_VOTE;

public class UsuarioServiceTeste {

    private final UsuarioService usuarioService= new UsuarioService();

    @Test
    public void testeCpfValido() {

        StepVerifier.create(usuarioService.validaCpf("05828575031"))
                .expectNext(StatusResposta.builder().resposta(ABLE_TO_VOTE).build())
                .expectComplete()
                .verify();
    }

    @Test
    public void testeCpfInvalido() {

        StepVerifier.create(usuarioService.validaCpf("11111111111"))
                .expectNext(StatusResposta.builder().resposta(UNABLE_TO_VOTE).build())
                .expectComplete()
                .verify();
    }
}
