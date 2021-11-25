package br.com.gustavo.votacao.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@AllArgsConstructor
public enum Escolha {

    SIM("sim", "Sim"), NAO("não", "Não"), INDEFINIDO("indefinido",null);

    private @Getter String value;

    private @Getter String escolha;

    @JsonCreator
    public static Escolha forValue(String escolha) {
        return Stream.of(values())
                .filter(t -> nonNull(t.getEscolha()))
                .filter(t -> t.getEscolha().equals(escolha))
                .findFirst()
                .orElseGet(() -> INDEFINIDO);
    }
}
