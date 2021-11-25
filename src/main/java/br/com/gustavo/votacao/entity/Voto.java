package br.com.gustavo.votacao.entity;

import br.com.gustavo.votacao.Enum.Escolha;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@XmlRootElement
public class Voto {

    @JsonProperty("pauta")
    private Pauta pauta;

    @JsonProperty("associado")
    private Associado associado;

    @JsonProperty("escolha")
    private Escolha escolha;
}
