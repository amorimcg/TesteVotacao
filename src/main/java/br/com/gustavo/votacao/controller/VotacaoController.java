package br.com.gustavo.votacao.controller;

import br.com.gustavo.votacao.entity.Pauta;
import br.com.gustavo.votacao.entity.Resultado;
import br.com.gustavo.votacao.entity.Voto;
import br.com.gustavo.votacao.mensagem.RabbitMQSender;
import br.com.gustavo.votacao.response.StatusResposta;
import br.com.gustavo.votacao.service.VotacaoService;
import br.com.gustavo.votacao.util.Utilidades;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/votacao")
@RestController
@RequiredArgsConstructor
public class VotacaoController {

    private final VotacaoService votacaoService;
    private final RabbitMQSender rabbitMqSender;

    @PostMapping(path = "/cadastraPauta")
    public Mono<StatusResposta> cadastraPauta(@RequestBody Pauta pauta) {
        return votacaoService.cadastraPauta(pauta);
    }

    @PostMapping(path = "/votar")
    public Mono<StatusResposta> cadastraVoto(@RequestBody Voto voto) {
        return votacaoService.cadastraVoto(voto, rabbitMqSender);
    }

    @PostMapping(path = "/abrirSessao")
    public Mono<StatusResposta> abrirSessao(@RequestBody Pauta pauta) {
        return Utilidades.criaSessao(pauta);
    }

    @PostMapping(path = "/sessaoAberta")
    public Mono<StatusResposta> verificarSessao(@RequestBody Pauta pauta) {
        return Utilidades.verificarSessaoAberta(pauta, rabbitMqSender);
    }

    @PostMapping(path = "/fecharSessao")
    public Mono<StatusResposta> fecharSessao(@RequestBody Pauta pauta) {
        return Utilidades.removerSessao(pauta);
    }

    @PostMapping(path = "/computarVotos")
    public Mono<Resultado> computarVotos(@RequestBody Pauta pauta) {
        return votacaoService.contabilizaVotos(pauta, rabbitMqSender);
    }
}
