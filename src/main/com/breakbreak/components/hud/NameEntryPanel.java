package main.com.breakbreak.components.hud;

import javax.swing.*;
import java.awt.*;

public class NameEntryPanel extends PanelComponent {

    private static final String NAME = "name-entry";
    private static final String PATH = "/name-panel.png";

    public static final int PANEL_WIDTH = 380;
    public static final int PANEL_HEIGHT = 320;

    private final JLabel titleLabel;
    private final JLabel scoreLabel;
    private final JLabel nameLabel;
    private final JTextField nameField;
    private final MaterialButton confirmButton;
    private int finalScore;

    public NameEntryPanel() {
        super(NAME, PATH, new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBounds(50, 210, PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(null);

        titleLabel = new JLabel("¡NUEVO REGISTRO!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBounds(20, 32, 340, 30);

        scoreLabel = new JLabel("Puntaje obtenido: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(20, 75, 340, 25);

        nameLabel = new JLabel("Ingresa tu nombre:", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Monospaced", Font.PLAIN, 15));
        nameLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setBounds(20, 118, 340, 25);

        nameField = new JTextField();
        nameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(30, 32, 36));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(218, 165, 32), 2));
        nameField.setBounds(65, 152, 250, 38);

        confirmButton = new MaterialButton("GUARDAR");
        confirmButton.setFont(new Font("Monospaced", Font.BOLD, 15));
        confirmButton.setBounds(100, 212, 180, 45);

        add(titleLabel);
        add(scoreLabel);
        add(nameLabel);
        add(nameField);
        add(confirmButton);

        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fondo oscuro sólido con borde dorado si aún no se carga name-panel.png
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(24, 26, 30, 245));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2d.setColor(new Color(218, 165, 32));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 16, 16);
        }
    }

    public void showWithScore(int score) {
        this.finalScore = score;
        this.scoreLabel.setText("Puntaje obtenido: " + score);
        this.nameField.setText("");
        setVisible(true);
        this.nameField.requestFocusInWindow();
    }

    public String getPlayerName() {
        return nameField.getText().trim();
    }

    public MaterialButton getConfirmButton() {
        return confirmButton;
    }

    public int getFinalScore() {
        return finalScore;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible && nameField != null) {
            nameField.setText("");
        }
    }
}
