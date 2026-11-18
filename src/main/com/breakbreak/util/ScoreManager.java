package main.com.breakbreak.util;

import java.io.*;
import java.util.*;

public class ScoreManager {

    public static final int MAX_SCORES = 20;
    private static final String FILE_NAME = "scores.score";

    private static ScoreManager instance;
    private final List<Score> scores;

    private ScoreManager() {
        scores = new ArrayList<>();
        load();
    }

    public static synchronized ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public void load() {
        scores.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            if (data instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Score s) {
                        scores.add(s);
                    }
                }
            }
            scores.sort((a, b) -> Integer.compare(b.score(), a.score()));
        } catch (Exception e) {
            System.err.println("[ScoreManager] Error al cargar scores.score: " + e.getMessage());
        }
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(new ArrayList<>(scores));
            System.out.println("[ScoreManager] Historial guardado correctamente en " + FILE_NAME);
        } catch (Exception e) {
            System.err.println("[ScoreManager] Error al guardar scores.score: " + e.getMessage());
        }
    }

    public void addScore(String playerName, int score) {
        String name = (playerName == null || playerName.trim().isEmpty()) ? "Jugador" : playerName.trim();
        scores.add(new Score(name, score));
        scores.sort((a, b) -> Integer.compare(b.score(), a.score()));

        while (scores.size() > MAX_SCORES) {
            scores.remove(scores.size() - 1);
        }
        save();
    }

    public List<Score> getTopScores() {
        return new ArrayList<>(scores);
    }

    public boolean isHighScore(int score) {
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        return score > scores.get(scores.size() - 1).score();
    }

    public int getHighestScore() {
        return scores.isEmpty() ? 0 : scores.get(0).score();
    }
}
