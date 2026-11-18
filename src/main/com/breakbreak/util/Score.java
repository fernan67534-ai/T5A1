package main.com.breakbreak.util;

import java.io.Serializable;

public record Score(String playerName, int score) implements Serializable {
    private static final long serialVersionUID = 1L;
}
