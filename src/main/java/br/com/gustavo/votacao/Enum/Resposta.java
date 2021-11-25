package br.com.gustavo.votacao.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum Resposta {

    ERRO("Erro"),
    SUCESSO("Sucesso"),
    ID_PAUTA_INEXISTENTE("Id Pauta Inexistente"),
    USUARIO_JA_VOTOU("Voto ja computado"),
    PAUTA_FECHADA("Pauta Fechada"),
    PAUTA_ABERTA("Pauta Aberta"),
    ABLE_TO_VOTE("CPF valido"),
    UNABLE_TO_VOTE("CPF invalido");

    private final String value;

    public static Resposta getFromValue(String value) {
        return Stream.of(Resposta.values())
                .filter(t -> t.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("invalid status: " + value));
    }
}
