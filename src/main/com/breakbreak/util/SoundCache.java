package main.com.breakbreak.util;

import javafx.scene.media.AudioClip;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class SoundCache {
    private static SoundCache instance;
    private final Map<String, AudioClip> cache;

    private SoundCache() {
        this.cache = new HashMap<>();
    }

    public static synchronized SoundCache getInstance() {
        if (instance == null) {
            instance = new SoundCache();
        }
        return instance;
    }

    public AudioClip getSound(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            URL url = getClass().getResource(path);
            String uri = null;

            if (url != null) {
                uri = url.toExternalForm();
            } else {
                // Respaldo híbrido por si IntelliJ tarda en sincronizar resources
                String cleanPath = path.startsWith("/") ? path.substring(1) : path;
                File localFile = new File("src/main/resources/" + cleanPath);
                if (localFile.exists()) {
                    uri = localFile.toURI().toString();
                }
            }

            if (uri != null) {
                AudioClip clip = new AudioClip(uri);
                cache.put(path, clip);
                return clip;
            } else {
                System.err.println("[SoundCache] Advertencia: No se encontró el efecto -> " + path);
            }
        } catch (Exception e) {
            System.err.println("[SoundCache] Error al cargar efecto: " + path);
        }
        return null;
    }
}
