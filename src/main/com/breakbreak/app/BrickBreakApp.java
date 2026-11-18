package main.com.breakbreak.app;

import main.com.breakbreak.components.*;
import main.com.breakbreak.components.gameplay.*;
import main.com.breakbreak.components.hud.*;
import main.com.breakbreak.enums.State;
import main.com.breakbreak.core.GameLoop;
import main.com.breakbreak.util.GameEventListener;
import main.com.breakbreak.util.MusicPlayer;
import main.com.breakbreak.util.ScoreManager;
import main.com.breakbreak.util.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class BrickBreakApp extends JFrame implements GameEventListener {

    private BackgroundComponent background;

    private final JLayeredPane gamePanel;
    private final JPanel sidebarPanel;

    private RecordPanel recordPanel;
    private ScorePanel scorePanel;
    private LivePanel livePanel;
    private LevelPanel levelPanel;

    private final JButton playButton;
    private final JButton resetButton;
    private final JButton exitButton;

    private PaddleComponent paddle;
    private BallComponent ball;
    private BorderComponent border;
    private BlockComponent[][] blocks;
    private List<BlockComponent> blockList = new ArrayList<>();
    private GameLoop gameLoop;
    private GameOverPanel gameOverPanel;
    private NameEntryPanel nameEntryPanel;
    private BonoSidebarPanel bonoSidebarPanel;

    private final ScoreManager scoreManager = ScoreManager.getInstance();
    private int currentRecord = 0;
    private int nivelProgreso = 1;

    public BrickBreakApp() {
        SoundPlayer.getInstance().preload();
        MusicPlayer.getInstance().preload();

        setTitle("BREAKBREAK - PARADIS EDITION");
        setSize(860, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(10, 10, 10));

        gamePanel = new JLayeredPane();
        gamePanel.setBounds(0, 0, 480, 740);
        gamePanel.setLayout(null);
        getContentPane().add(gamePanel);

        sidebarPanel = new JPanel();
        sidebarPanel.setBounds(480, 0, 380, 740);
        sidebarPanel.setBackground(new Color(20, 21, 23));
        sidebarPanel.setLayout(null);
        sidebarPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        getContentPane().add(sidebarPanel);

        border = new BorderComponent(new Rectangle(0, 0, 480, 740));
        Rectangle area = border.getInnerBounds();

        ball = new BallComponent(227, 490, 26, 26);
        paddle = new PaddleComponent(195, 600, 90, 20);

        background = new BackgroundComponent(480, 740);
        gamePanel.add(background, Integer.valueOf(-10));

        String[] matrixColors = {"blue", "orange", "yellow", "aqua", "cyan", "green"};
        this.blocks = new BlockComponent[7][6];

        int blockW = 69, blockH = 20;
        int gridStartX = area.x + 3;
        int gridStartY = area.y + 15;

        java.util.Random randomCromatico = new java.util.Random();

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 6; c++) {
                int bx = gridStartX + (c * blockW);
                int by = gridStartY + (r * blockH);

                String colorAleatorio = matrixColors[randomCromatico.nextInt(matrixColors.length)];
                BlockComponent block = new BlockComponent(bx, by, blockW, blockH, colorAleatorio);

                this.blocks[r][c] = block;
                blockList.add(block);
                gamePanel.add(block);
            }
        }

        gamePanel.add(ball, Integer.valueOf(0));
        gamePanel.add(paddle, Integer.valueOf(0));
        gamePanel.add(border, Integer.valueOf(5));

        this.setFocusable(true);
        this.requestFocusInWindow();
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        gamePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (gameLoop != null && (gameLoop.getState() == State.RUNNING || gameLoop.getState() == State.READY)) {
                    int newX = e.getX() - (paddle.getWidth() / 2);
                    if (newX < area.x) newX = area.x;
                    if (newX + paddle.getWidth() > area.x + area.width) newX = area.x + area.width - paddle.getWidth();
                    paddle.setLocation(newX, paddle.getY());
                }
            }
        });

        gamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (gameLoop != null) {
                    gameLoop.lanzarPelotaMagnetica();
                }
            }
        });
        gameOverPanel = new GameOverPanel(0, 0, 480, 740);
        gamePanel.add(gameOverPanel, Integer.valueOf(10));

        // Panel emergente de ingreso de nombre al perder (Entregable 6)
        nameEntryPanel = new NameEntryPanel();
        gamePanel.add(nameEntryPanel, Integer.valueOf(20));
        nameEntryPanel.getConfirmButton().addActionListener(e -> saveScoreAndShowGameOver());

        recordPanel = new RecordPanel();
        scorePanel = new ScorePanel();
        livePanel = new LivePanel();
        levelPanel = new LevelPanel();
        bonoSidebarPanel = new BonoSidebarPanel();

        playButton = new MaterialButton("JUGAR");
        playButton.setFont(new Font("Monospaced", Font.BOLD, 12));
        playButton.setFocusable(false);

        resetButton = new MaterialButton("REINICIAR");
        resetButton.setFont(new Font("Monospaced", Font.BOLD, 12));
        resetButton.setFocusable(false);
        resetButton.setEnabled(false);

        exitButton = new MaterialButton("SALIR");
        exitButton.setFont(new Font("Monospaced", Font.BOLD, 12));
        exitButton.setFocusable(false);

        // Posiciones exactas en el panel derecho
        recordPanel.setBounds(46, 8, 288, 70);
        scorePanel.setBounds(46, 85, 288, 136);
        livePanel.setBounds(66, 228, 248, 136);
        levelPanel.setBounds(70, 372, 240, 136);
        bonoSidebarPanel.setBounds(30, 518, 320, 65);

        sidebarPanel.add(recordPanel);
        sidebarPanel.add(scorePanel);
        sidebarPanel.add(livePanel);
        sidebarPanel.add(levelPanel);
        sidebarPanel.add(bonoSidebarPanel);

        // Botones equilibrados con ancho suficiente para leer REINICIAR completo
        playButton.setBounds(25, 595, 102, 40);
        resetButton.setBounds(137, 595, 106, 40);
        exitButton.setBounds(253, 595, 102, 40);

        sidebarPanel.add(playButton);
        sidebarPanel.add(resetButton);
        sidebarPanel.add(exitButton);

        levelPanel.setLevel(nivelProgreso);

        gameLoop = new GameLoop(gamePanel, ball, paddle, border, blocks, new ArrayList<>());
        gameLoop.addListener(this);
        gameLoop.setBonoSidebarPanel(bonoSidebarPanel);

        // =========================================================================
        // 🏁 LISTENERS OPERATIVOS CON CONTROL DE MÚSICA INTEGRADO
        // =========================================================================
        playButton.addActionListener(e -> {
            if (gameLoop != null) {
                if (gameLoop.getState() == main.com.breakbreak.enums.State.RUNNING) {
                    gameLoop.pause();
                    MusicPlayer.getInstance().stop();
                    playButton.setText("REANUDAR");
                } else {
                    gameLoop.start();
                    MusicPlayer.getInstance().playLevelMusic();
                    playButton.setText("PAUSA");
                    resetButton.setEnabled(true);
                }
            }
            gamePanel.requestFocusInWindow();
        });

        resetButton.addActionListener(e -> {
            if (gameLoop != null) {
                gameLoop.resetGame();
            }
            gamePanel.requestFocusInWindow();
        });

        exitButton.addActionListener(e -> {
            MusicPlayer.getInstance().stop();
            System.exit(0);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });

        // Cargar historial persistente desde scores.score al iniciar (Entregable 6)
        scoreManager.load();
        this.currentRecord = scoreManager.getHighestScore();
        recordPanel.setRecord(this.currentRecord);
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE LA INTERFAZ DE EVENTOS (LISTENERS CON HUD)
    // =========================================================================

    @Override
    public void onScoreChanged(int currentScore) {
        scorePanel.setScore(currentScore);

        if (currentScore > currentRecord) {
            currentRecord = currentScore;
            recordPanel.setRecord(currentRecord);
            recordPanel.repaint();
        }
    }

    @Override
    public void onLifeLost(int currentLives) {
        livePanel.setLives(currentLives);
        playButton.setText("REANUDAR");
        gamePanel.requestFocusInWindow();
    }

    @Override
    public void onLevelStarted(int levelNumber) {
        this.nivelProgreso = levelNumber;
        levelPanel.setLevel(levelNumber);
    }

    @Override
    public void onCollisionOccurred(main.com.breakbreak.enums.CollisionType type) {}

    @Override
    public void onVictory(int finalScore) {
        MusicPlayer.getInstance().playWinMusic();

        playButton.setEnabled(false);
        playButton.setText("¡VICTORIA!");

        resetButton.setEnabled(true);
        resetButton.setText("SIG. NIVEL");

        for (java.awt.event.ActionListener al : resetButton.getActionListeners()) {
            resetButton.removeActionListener(al);
        }

        resetButton.addActionListener(e -> avanzarSiguienteNivel());

        WinPanel winPanel = new WinPanel(finalScore);
        // Igualar dimensiones exactas con GameOverPanel (0, 0, 480, 740)
        winPanel.setBounds(0, 0, 480, 740);
        gamePanel.add(winPanel, Integer.valueOf(15));
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    @Override
    public void onGameOver(int finalScore) {
        // Primero muestra el panel para que el jugador ingrese su nombre (Entregable 6)
        showNameEntry(finalScore);
    }

    private void showNameEntry(int score) {
        playButton.setEnabled(false);
        playButton.setText("DERROTA");
        resetButton.setEnabled(false);

        nameEntryPanel.showWithScore(score);
        gamePanel.setComponentZOrder(nameEntryPanel, 0);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private void saveScoreAndShowGameOver() {
        String nombre = nameEntryPanel.getPlayerName();
        if (nombre.isEmpty()) {
            nombre = "Jugador";
        }
        int puntaje = nameEntryPanel.getFinalScore();

        // Guardar en scores.score (ordena y mantiene Top 20) y actualizar el récord visual
        scoreManager.addScore(nombre, puntaje);
        this.currentRecord = scoreManager.getHighestScore();
        recordPanel.setRecord(this.currentRecord);
        recordPanel.repaint();

        // Ocultar el panel de nombre y mostrar la pantalla final de GameOver con su música
        nameEntryPanel.setVisible(false);
        MusicPlayer.getInstance().playGameOverMusic();

        gamePanel.setComponentZOrder(gameOverPanel, 0);
        gameOverPanel.setVisible(true);

        resetButton.setEnabled(true);
        resetButton.setText("REINICIAR");
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    @Override
    public void onReset() {
        MusicPlayer.getInstance().stop();

        scorePanel.setScore(0);
        livePanel.setLives(3);
        // Nota: currentRecord se conserva intacto en el HUD

        this.nivelProgreso = 1;
        levelPanel.setLevel(1);

        gameOverPanel.setVisible(false);
        if (nameEntryPanel != null) {
            nameEntryPanel.setVisible(false);
        }

        for (java.awt.event.ActionListener al : resetButton.getActionListeners()) {
            resetButton.removeActionListener(al);
        }
        resetButton.addActionListener(e -> {
            if (gameLoop != null) gameLoop.resetGame();
            gamePanel.requestFocusInWindow();
        });

        playButton.setText("JUGAR");
        playButton.setEnabled(true);
        resetButton.setText("REINICIAR");
        resetButton.setEnabled(false);

        if (bonoSidebarPanel != null) {
            bonoSidebarPanel.reset();
        }

        if (background != null) {
            background.cambiarFondoPorNivel(1);
        }

        for (Component c : gamePanel.getComponents()) {
            if (c instanceof WinPanel) {
                gamePanel.remove(c);
            }
        }

        ball.setVisible(true);
        paddle.setVisible(true);
        gamePanel.revalidate();
        gamePanel.repaint();
        gamePanel.requestFocusInWindow();
    }

    /**
     * Avanza al siguiente nivel, actualiza el LevelPanel e inicia una nueva pista musical aleatoria.
     */
    public void avanzarSiguienteNivel() {
        nivelProgreso++;

        // 1. Sincronizar el incremento numérico en el LevelPanel
        this.onLevelStarted(nivelProgreso);

        // 2. Iniciar nueva pista musical aleatoria distinta a la anterior
        MusicPlayer.getInstance().playLevelMusic();

        if (background != null) {
            background.cambiarFondoPorNivel(nivelProgreso);
        }

        // 3. Limpiar todos los bloques remanentes de Swing del JLayeredPane izquierdo
        if (this.blocks != null) {
            for (int r = 0; r < 7; r++) {
                for (int c = 0; c < 6; c++) {
                    if (this.blocks[r][c] != null) {
                        gamePanel.remove(this.blocks[r][c]);
                    }
                }
            }
        }

        // 4. Solicitar a la fábrica la construcción del mapa procedural en la matriz bidimensional
        Rectangle area = border.getInnerBounds();
        this.blocks = main.com.breakbreak.core.BlockFactory.generarNivelInfinito(
                nivelProgreso, 69, 20, area.x + 3, area.y + 15
        );

        // 5. Poblar las colecciones e inyectar cada bloque al contenedor gráfico base
        blockList.clear();
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 6; c++) {
                BlockComponent b = this.blocks[r][c];
                blockList.add(b);
                gamePanel.add(b, Integer.valueOf(0));
            }
        }

        // 6. Reiniciar coordenadas físicas y pasar la nueva matriz de memoria al GameLoop
        gameLoop.resetGameParaSiguienteNivel(this.blocks);

        // 7. Restaurar el botón de reinicio estándar de la botonera
        for (java.awt.event.ActionListener al : resetButton.getActionListeners()) {
            resetButton.removeActionListener(al);
        }
        resetButton.addActionListener(e -> {
            if (gameLoop != null) gameLoop.resetGame();
            gamePanel.requestFocusInWindow();
        });

        resetButton.setText("REINICIAR");
        resetButton.setEnabled(false);
        playButton.setText("JUGAR");
        playButton.setEnabled(true);

        // Remoción del letrero gráfico de victoria del nivel anterior
        for (Component c : gamePanel.getComponents()) {
            if (c instanceof WinPanel) {
                gamePanel.remove(c);
            }
        }

        gamePanel.revalidate();
        gamePanel.repaint();
        gamePanel.requestFocusInWindow();
    }
}
