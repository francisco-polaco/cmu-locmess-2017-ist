package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diogo on 20/04/2017.
 */

public class User {

    private String username;
    private String password;
    private List<Pair> pairs;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        pairs = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }
}
