package br.com.gustavo.votacao.util;

import br.com.gustavo.votacao.Enum.Resposta;
import br.com.gustavo.votacao.constantes.Constantes;
import br.com.gustavo.votacao.entity.Pauta;
import br.com.gustavo.votacao.entity.Resultado;
import br.com.gustavo.votacao.entity.Voto;
import br.com.gustavo.votacao.mensagem.RabbitMQSender;
import br.com.gustavo.votacao.response.StatusResposta;
import br.com.gustavo.votacao.sessao.SessionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.gustavo.votacao.constantes.Constantes.CONST_VOTO_DIR;

@Slf4j
@RequiredArgsConstructor
public class Utilidades {

    public static Mono<StatusResposta> geraXml(Object objetc, String fileName) {

        try {
            JAXBContext contextObj = JAXBContext.newInstance(Voto.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshallerObj.marshal(objetc, new FileOutputStream(fileName));

            return Mono.just(StatusResposta.builder().resposta(Resposta.SUCESSO).build());
        } catch (JAXBException | FileNotFoundException e) {
            log.error("Erro ao gerar arquivo {}", fileName);
            return Mono.just(StatusResposta.builder().resposta(Resposta.ERRO).build());
        }

    }

    public static Mono<Boolean> validaExistente(String dirName, String fileName) throws IOException {
        try {
            return Mono.just(dirName != null && Arrays.stream(Objects.requireNonNull(new File(dirName).listFiles()))
                    .anyMatch(file -> file.getName().equals(fileName)));
        } catch (Exception e) {
            log.error("Erro ao validar arquivo existente {}", fileName, e);
            return Mono.just(false);
        }
    }

    public static Mono<StatusResposta> criaSessao(Pauta pauta) {
        SessionContext session = SessionContext.getInstance();
        session.getSessao().put(pauta.getId(), ZonedDateTime.now());
        return Mono.just(StatusResposta.builder().resposta(Resposta.SUCESSO).build());

    }

    public static Mono<StatusResposta> verificarSessaoAberta(Pauta pauta, RabbitMQSender rabbitMqSender) {
        SessionContext session = SessionContext.getInstance();
        if(!session.getSessao().containsKey(pauta.getId())){
            log.debug("Pauta {} esta fechada", pauta.getNomePauta());

            return Mono.just(StatusResposta.builder().resposta(Resposta.PAUTA_FECHADA).build());
        }

        ZonedDateTime data = session.getSessao().get(pauta.getId());
        Duration duration = Duration.between(data, ZonedDateTime.now());
        if(duration.getSeconds() < 60) {
            log.debug("Pauta {} esta aberta", pauta.getNomePauta());
            return Mono.just(StatusResposta.builder().resposta(Resposta.PAUTA_ABERTA).build());
        }
        log.debug("Pauta {} esta fechada", pauta.getNomePauta());
        removerSessao(pauta);
        try {
            rabbitMqSender.send(Objects.requireNonNull(contabilizaResultado(CONST_VOTO_DIR, pauta)));
        } catch (IOException e) {
            log.error("Erro ao tentar enviar a contabilizacao do resultado para a fila");
        }
        return Mono.just(StatusResposta.builder().resposta(Resposta.PAUTA_FECHADA).build());
    }

    public static Mono<StatusResposta> removerSessao(Pauta pauta) {
        SessionContext session = SessionContext.getInstance();
        session.getSessao().remove(pauta.getId());
        return Mono.just(StatusResposta.builder().resposta(Resposta.SUCESSO).build());
    }

    public static Mono<Boolean> isCPF(String CPF) {

        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") ||
                CPF.equals("33333333333") ||
                CPF.equals("44444444444") ||
                CPF.equals("55555555555") ||
                CPF.equals("66666666666") ||
                CPF.equals("77777777777") ||
                CPF.equals("88888888888") ||
                CPF.equals("99999999999") ||
                (CPF.length() != 11)) {
            return Mono.just(false);
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                num = CPF.charAt(i) - 48;
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = CPF.charAt(i) - 48;
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            return Mono.just((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)));

        } catch (InputMismatchException erro) {
            log.error("Erro ao tentar validar o CPF do usuario");
            return Mono.just(false);
        }
    }

    public static Mono<Resultado> contabilizaResultado(String dirName, Pauta pauta) throws IOException {
        try {
            Resultado resultado = new Resultado(pauta);
            Arrays.stream(Objects.requireNonNull(new File(dirName).listFiles()))
                    .filter(file -> file.getName().contains(String.valueOf(pauta.getId()).concat(Constantes.CONST_SEPARADOR_NOME_ARQUIVO)))
                    .forEach(file -> {
                        try {
                            Unmarshaller jaxbUnmarshaller;
                            JAXBContext jaxbContext = JAXBContext.newInstance(Voto.class);
                            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                            Voto voto = (Voto) jaxbUnmarshaller.unmarshal(new File(file.getAbsolutePath()));
                            resultado.incrementaResultado(voto);
                        } catch (JAXBException e) {
                            log.error("Erro ao validar contabilizar votos para a pauta {}", pauta.getNomePauta(), e);
                        }
                    });

            return Mono.just(resultado);
        } catch (Exception e) {
            log.error("Erro ao validar contabilizar votos para a pauta {}", pauta.getNomePauta(), e);
            return null;
        }
    }

    public static List<Pauta> retornaPautas(String dirNamePauta) throws IOException {
        try {
            return Arrays.stream(Objects.requireNonNull(new File(dirNamePauta).listFiles()))
                    .map(file -> {
                        try {
                            Unmarshaller jaxbUnmarshaller;
                            JAXBContext jaxbContext = JAXBContext.newInstance(Pauta.class);
                            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                            return (Pauta) jaxbUnmarshaller.unmarshal(new File(file.getAbsolutePath()));
                        } catch (JAXBException e) {
                            log.error("Erro ao recuperar pautas no diretorio {}", dirNamePauta, e);
                            return null;
                        }
                    }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Erro ao recuperar pautas no diretorio {}", dirNamePauta, e);
            return null;
        }
    }
}
