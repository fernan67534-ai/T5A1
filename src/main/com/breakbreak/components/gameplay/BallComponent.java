package main.com.breakbreak.components.gameplay;

import main.com.breakbreak.components.GameComponent;
import main.com.breakbreak.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class BallComponent extends GameComponent {
    public static final int BALL_SIZE = 20;
    public static final int SPRITE_COUNT = 4;
    public static final int BALL_SPEED_X = 4;
    public static final int BALL_SPEED_Y = 4;

    private static final String[] PERSONAJE_NAMES = {"ball", "armin", "mikasa", "fireball"};
    private static final String[] PERSONAJE_FILES = {"ball.gif", "armin.gif", "mikasa.gif", "fireball.gif"};

    private final String selectedImageName;
    private ImageIcon fallbackIcon;
    private double xPos;
    private double yPos;
    private int vx = BALL_SPEED_X;
    private int vy = -BALL_SPEED_Y;

    public BallComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.xPos = x;
        this.yPos = y;

        // Elige al azar uno de los 4 personajes al crearse y lo mantiene fijo
        int spriteIndex = new Random().nextInt(SPRITE_COUNT);
        this.selectedImageName = PERSONAJE_NAMES[spriteIndex];
        String fileName = PERSONAJE_FILES[spriteIndex];

        // 1. Intento estándar mediante ImageCache
        setImage(this.selectedImageName, "/" + fileName);

        // 2. Respaldo directo desde disco duro si el classpath de IntelliJ falla
        if (getImage() == null) {
            File localFile = new File("src/main/resources/" + fileName);
            if (!localFile.exists()) {
                localFile = new File("src/main/resources/image/" + fileName);
            }
            if (localFile.exists()) {
                this.fallbackIcon = new ImageIcon(localFile.getAbsolutePath());
                System.out.println("[BallComponent] Éxito Manual: Cargado desde disco -> " + localFile.getAbsolutePath());
            } else {
                System.err.println("[BallComponent] Advertencia: No se encontró -> " + fileName);
            }
        }
    }

    public void updatePosition() {
        setPosition(this.xPos + this.vx, this.yPos + this.vy);
    }

    public int getVx() { return vx; }
    public void setVx(int vx) { this.vx = vx; }
    public int getVy() { return vy; }
    public void setVy(int vy) { this.vy = vy; }
    public double getXPos() { return xPos; }
    public double getYPos() { return yPos; }

    public void setX(int x) {
        this.xPos = x;
        setLocation((int) Math.round(xPos), (int) Math.round(yPos));
    }

    public void setY(int y) {
        this.yPos = y;
        setLocation((int) Math.round(xPos), (int) Math.round(yPos));
    }

    public void setPosition(double x, double y) {
        this.xPos = x;
        this.yPos = y;
        setLocation((int) Math.round(xPos), (int) Math.round(yPos));
    }

    @Override
    public ImageIcon getImage() {
        ImageIcon cached = ImageCache.getInstance().getImage(selectedImageName);
        return (cached != null) ? cached : fallbackIcon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        ImageIcon img = getImage();
        if (img != null) {
            g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            super.paintComponent(g);
        }
    }
}
