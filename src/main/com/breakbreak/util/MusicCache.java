package main.com.breakbreak.util;

import javafx.scene.media.Media;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class MusicCache {
    private static MusicCache instance;
    private final Map<String, Media> cache;

    private MusicCache() {
        this.cache = new HashMap<>();
    }

    public static synchronized MusicCache getInstance() {
        if (instance == null) {
            instance = new MusicCache();
        }
        return instance;
    }

    public Media getMedia(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            URL url = getClass().getResource(path);
            String uri = null;

            if (url != null) {
                uri = url.toExternalForm();
            } else {
                String cleanPath = path.startsWith("/") ? path.substring(1) : path;
                File localFile = new File("src/main/resources/" + cleanPath);
                if (localFile.exists()) {
                    uri = localFile.toURI().toString();
                }
            }

            if (uri != null) {
                Media media = new Media(uri);
                cache.put(path, media);
                return media;
            } else {
                System.err.println("[MusicCache] Advertencia: No se encontró la pista -> " + path);
            }
        } catch (Exception e) {
            System.err.println("[MusicCache] Error al cargar pista: " + path);
        }
        return null;
    }
}
