package br.com.gustavo.votacao.sessao;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.HashMap;

@Data
public class SessionContext {

    private static SessionContext instance;

    private HashMap<Integer, ZonedDateTime> sessao = new HashMap<>();

    public static SessionContext getInstance(){
        if (instance == null){
            instance = new SessionContext();
        }

        return instance;
    }



}
