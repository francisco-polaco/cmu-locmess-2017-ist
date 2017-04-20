package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import java.io.Serializable;

/**
 * Created by Diogo on 20/04/2017.
 */

public class Token implements Serializable {
    private static final long serialVersionUID = 1;

    private String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
