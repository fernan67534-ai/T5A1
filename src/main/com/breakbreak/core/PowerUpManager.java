package main.com.breakbreak.core;

import main.com.breakbreak.components.gameplay.BlockComponent;
import main.com.breakbreak.components.gameplay.BorderComponent;
import main.com.breakbreak.components.gameplay.PaddleComponent;
import main.com.breakbreak.components.gameplay.PowerUpComponent;
import main.com.breakbreak.enums.CollisionType;
import main.com.breakbreak.enums.PowerUpType;
import main.com.breakbreak.events.CollisionEvent;
import main.com.breakbreak.events.PowerUpRegistry;
import main.com.breakbreak.util.CollisionObserver;
import main.com.breakbreak.util.SoundPlayer;

import javax.swing.JComponent;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PowerUpManager implements CollisionObserver {

    private final JComponent host;
    private final PaddleComponent paddle;
    private final BorderComponent border;
    private final List<PowerUpComponent> activePowerUps = new ArrayList<>();
    private final Random random = new Random();

    // Almacena la referencia de control al bucle principal de juego para alterar físicas
    private GameLoop gameLoop;

    public PowerUpManager(JComponent host, PaddleComponent paddle, BorderComponent border) {
        this.host = host;
        this.paddle = paddle;
        this.border = border;
    }

    /**
     * Vincula el bucle central al gestor de bonos de manera segura tras la construcción.
     */
    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    @Override
    public void onCollision(CollisionEvent event) {
        if (event.type() != CollisionType.BALL_BLOCK) return;
        if (!(event.second() instanceof BlockComponent block) || !block.isDestroyed()) return;

        // Probabilidad del 35% de generar un bono al romperse el bloque
        if (random.nextDouble() >= 0.35) return;
        spawnPowerUpAt(block);
    }

    public void checkAndSpawn(BlockComponent block) {
        if (random.nextDouble() >= 0.35) return;
        spawnPowerUpAt(block);
    }

    /**
     * UNIFICADO Y CORREGIDO: Centraliza la creación y aplica el centrado milimétrico
     * basado en los 44 píxeles reales de renderizado del nuevo sprite.
     */
    private void spawnPowerUpAt(BlockComponent block) {
        PowerUpType type = PowerUpType.values()[random.nextInt(PowerUpType.values().length)];

        // Fórmula de centrado exacta: Mitad del bloque menos la mitad del tamaño de render del poder
        int blockCenter = block.getX() + (block.getWidth() / 2);
        int spawnX = blockCenter - (PowerUpComponent.RENDER_WIDTH / 2);
        int spawnY = block.getY() + block.getHeight();

        Rectangle innerArea = border.getInnerBounds();

        if (spawnX < innerArea.x) {
            spawnX = innerArea.x;
        } else if (spawnX > innerArea.x + innerArea.width - PowerUpComponent.RENDER_WIDTH) {
            spawnX = innerArea.x + innerArea.width - PowerUpComponent.RENDER_WIDTH;
        }

        PowerUpComponent powerUp = new PowerUpComponent(type, spawnX, spawnY);
        activePowerUps.add(powerUp);
        host.add(powerUp, Integer.valueOf(0));
        host.repaint();
    }

    public void update() {
        Rectangle paddleBounds = paddle.getBounds();
        double maxY = border.getInnerBounds().getMaxY();

        for (PowerUpComponent powerUp : List.copyOf(activePowerUps)) {
            powerUp.updatePosition();

            if (powerUp.getBounds().intersects(paddleBounds)) {
                applyEffect(powerUp);
                removePowerUp(powerUp);
            } else if (powerUp.getY() > maxY) {
                removePowerUp(powerUp);
            }
        }
    }

    private void removePowerUp(PowerUpComponent powerUp) {
        activePowerUps.remove(powerUp);
        host.remove(powerUp);
        host.repaint();
    }

    /**
     * Invoca el sonido de captura de bono y aplica el efecto polimórfico al motor.
     */
    private void applyEffect(PowerUpComponent powerUp) {
        // Dispara el efecto de sonido de captura de power-up (Entregable 5)
        SoundPlayer.getInstance().playBonus();

        PowerUpType type = powerUp.getType();
        System.out.println("Bono Capturado Especial: " + type.name());

        if (this.gameLoop != null) {
            PowerUpRegistry.getAction(type).apply(this.gameLoop);
        }
    }

    public void clear() {
        for (PowerUpComponent powerUp : List.copyOf(activePowerUps)) {
            removePowerUp(powerUp);
        }
        activePowerUps.clear();
    }
}
