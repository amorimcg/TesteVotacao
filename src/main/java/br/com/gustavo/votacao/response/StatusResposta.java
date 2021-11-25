package br.com.gustavo.votacao.response;

import br.com.gustavo.votacao.Enum.Resposta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class StatusResposta {
    private Resposta resposta;
}
