package main.com.breakbreak.components.hud;

import java.awt.Dimension;
import java.net.URL;

public class RecordPanel extends PanelComponent {
    private final NumberComponent recordDisplay;

    public RecordPanel() {
        // Tamaño estándar unificado para el HUD
        super("record", obtenerRutaSegura(), new Dimension(288, 70));

        // Centrado horizontalmente en la parte inferior del marco de récord
        // X: 64, Y: 36 (debajo de la palabra RECORD), Ancho: 160, Alto: 24
        this.recordDisplay = new NumberComponent(6, 64, 36, 160, 24);
        this.add(recordDisplay);
    }

    private static String obtenerRutaSegura() {
        String[] rutasPosibles = new String[] {
                "/record.png",
                "/images/record.png",
                "/image/record.png"
        };
        for (String ruta : rutasPosibles) {
            URL url = RecordPanel.class.getResource(ruta);
            if (url != null) return ruta;
        }
        return "/record.png";
    }

    public void setRecord(int record) {
        this.recordDisplay.setValue(record);
        this.recordDisplay.repaint();
    }
}
