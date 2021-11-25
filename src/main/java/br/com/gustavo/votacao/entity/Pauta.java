package br.com.gustavo.votacao.entity;

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
public class Pauta {

    @JsonProperty("id")
    private int id;

    @JsonProperty("nomePauta")
    private String nomePauta;
}
