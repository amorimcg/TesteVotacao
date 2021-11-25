package br.com.gustavo.votacao.controller;

import br.com.gustavo.votacao.response.StatusResposta;
import br.com.gustavo.votacao.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping(path = "/users")
    public Mono<StatusResposta> validaCpf(@RequestParam("cpf") String cpf) {
        return usuarioService.validaCpf(cpf);
    }
}
