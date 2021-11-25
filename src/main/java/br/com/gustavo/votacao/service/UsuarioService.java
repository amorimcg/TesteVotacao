package br.com.gustavo.votacao.service;

import br.com.gustavo.votacao.response.StatusResposta;
import br.com.gustavo.votacao.util.Utilidades;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static br.com.gustavo.votacao.Enum.Resposta.ABLE_TO_VOTE;
import static br.com.gustavo.votacao.Enum.Resposta.UNABLE_TO_VOTE;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsuarioService {

    public Mono<StatusResposta> validaCpf(String cpf) {
        log.debug("Inciando validacao do cpf {}", cpf);
        return Utilidades.isCPF(cpf)
                .filter(retorno -> retorno)
                .flatMap(retorno -> Mono.just(StatusResposta.builder().resposta(ABLE_TO_VOTE).build()))
                .switchIfEmpty(Mono.just(StatusResposta.builder().resposta(UNABLE_TO_VOTE).build()));
    }
}
