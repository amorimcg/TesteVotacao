package br.com.gustavo.votacao.service;

import br.com.gustavo.votacao.Enum.Resposta;
import br.com.gustavo.votacao.entity.Pauta;
import br.com.gustavo.votacao.entity.Resultado;
import br.com.gustavo.votacao.entity.Voto;
import br.com.gustavo.votacao.mensagem.RabbitMQSender;
import br.com.gustavo.votacao.response.StatusResposta;
import br.com.gustavo.votacao.util.Utilidades;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

import static br.com.gustavo.votacao.constantes.Constantes.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class VotacaoService {

    public Mono<StatusResposta> cadastraPauta(Pauta pauta) {

            log.debug("Iniciando cadastro da pauta {}", pauta.getNomePauta());
            return Mono.just(pauta)
                    .flatMap(pt -> Utilidades.geraXml(pt, CONST_PAUTA_DIR.concat(geraNomeArquivoPauta(pt))));
    }

    public Mono<StatusResposta> cadastraVoto(Voto voto, RabbitMQSender rabbitMqSender) {

        log.debug("Iniciando cadastro do voto para a pauta {}", voto.getPauta().getNomePauta());

        Mono<StatusResposta> statusResposta = validaStatusVotacao(voto, rabbitMqSender);
        return Mono.just(voto)
            .zipWith(statusResposta)
            .flatMap(val3 -> computaVoto(val3.getT1(), val3.getT2()))
            .doOnError(error -> log.error("Houve um erro ao cadastrar voto para a pauta {}.", voto.getPauta().getNomePauta(), error));
    }

    public Mono<Resultado> contabilizaVotos(Pauta pauta, RabbitMQSender rabbitMqSender) {

        log.debug("Contabilizando votos para a pauta {}", pauta.getNomePauta());
        return Mono.just(pauta)
                .flatMap(pt -> contabilizaEnviaResultadoFila(pauta, rabbitMqSender, pt));
    }

    private Mono<StatusResposta> computaVoto(Voto voto, StatusResposta statusResposta) {
        if(statusResposta.getResposta() == Resposta.SUCESSO) {
            return Utilidades.geraXml(voto, CONST_VOTO_DIR.concat(geraNomeArquivoVoto(voto)));
        } else {
            return Mono.just(statusResposta);
        }
    }

    private Mono<StatusResposta> validaStatusVotacao(Voto voto, RabbitMQSender rabbitMqSender) {
        try {
            Mono<StatusResposta> checkPauta = Utilidades.verificarSessaoAberta(voto.getPauta(), rabbitMqSender);
            Mono<Boolean> pautaExistente = Utilidades.validaExistente(CONST_PAUTA_DIR, geraNomeArquivoPauta(voto.getPauta()));
            Mono<Boolean> usuarioVotou = Utilidades.validaExistente(CONST_VOTO_DIR, geraNomeArquivoVoto(voto));

            return Mono.zip(checkPauta, pautaExistente, usuarioVotou)
                    .flatMap(data -> {

                        if(data.getT1().getResposta() == Resposta.PAUTA_FECHADA) {
                            return Mono.just(StatusResposta.builder().resposta(Resposta.PAUTA_FECHADA).build());
                        }

                        if(!data.getT2()) {
                            return Mono.just(StatusResposta.builder().resposta(Resposta.ID_PAUTA_INEXISTENTE).build());
                        }

                        if(data.getT3()) {
                            return Mono.just(StatusResposta.builder().resposta(Resposta.USUARIO_JA_VOTOU).build());
                        }

                        return Mono.just(StatusResposta.builder().resposta(Resposta.SUCESSO).build());
                    });

        } catch (IOException e) {
            log.error("Erro ao computar voto para a pauta {}", voto.getPauta().getNomePauta(), e);
            return Mono.just(StatusResposta.builder().resposta(Resposta.ERRO).build());
        }
    }

    private Mono<Resultado> contabilizaEnviaResultadoFila(Pauta pauta, RabbitMQSender rabbitMqSender, Pauta pt) {
        try {
            return rabbitMqSender.send(Objects.requireNonNull(Utilidades.contabilizaResultado(CONST_VOTO_DIR, pt)));
        } catch (IOException e) {
            log.error("Erro ao validar contabilizar votos para a pauta {}", pauta.getNomePauta(), e);
            return Mono.error(new Exception("Erro ao validar contabilizar votos para a pauta {}"));
        }
    }

    private String geraNomeArquivoPauta(Pauta pauta) {
        return String.valueOf(pauta.getId()).concat(CONST_EXT_ARQUIVO_XML);
    }

    private String geraNomeArquivoVoto(Voto voto){
        return String.valueOf(voto.getPauta().getId())
                .concat(CONST_SEPARADOR_NOME_ARQUIVO)
                .concat(voto.getAssociado().getCpf())
                .concat(CONST_EXT_ARQUIVO_XML);
    }
}
