package br.com.gustavo.votacao.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Resultado extends Pauta {

    @JsonProperty("sim")
    private long sim;

    @JsonProperty("nao")
    private long nao;

    public Resultado (Pauta pauta) {
        super(pauta.getId(), pauta.getNomePauta());
    }

    public void incrementaResultado(Voto voto) {

        switch (voto.getEscolha()) {
            case SIM:
                this.sim++;
                break;
            case NAO:
                this.nao++;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}
