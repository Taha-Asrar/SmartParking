package smartparking.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Représentation graphique d'une voiture comme un Group JavaFX.
 * Dimensions : 64 x 36 pixels (orientée horizontalement).
 */
public class CarShape extends Group {

    public static final double CAR_W = 64;
    public static final double CAR_H = 36;

    private Text plateText;

    public CarShape(String immatriculation, String hexColor) {
        Color bodyColor = Color.web(hexColor);
        Color darkBody  = bodyColor.darker();
        Color roofColor = bodyColor.brighter();

        // --- Carrosserie principale ---
        Rectangle body = new Rectangle(0, 10, CAR_W, 20);
        body.setArcWidth(10);
        body.setArcHeight(10);
        body.setFill(bodyColor);
        body.setStroke(darkBody);
        body.setStrokeWidth(1.5);

        // --- Toit ---
        Rectangle roof = new Rectangle(14, 2, 36, 14);
        roof.setArcWidth(8);
        roof.setArcHeight(8);
        roof.setFill(roofColor);
        roof.setStroke(darkBody);
        roof.setStrokeWidth(1);

        // --- Vitre avant ---
        Rectangle winFront = new Rectangle(46, 4, 10, 10);
        winFront.setArcWidth(4);
        winFront.setArcHeight(4);
        winFront.setFill(Color.rgb(173, 216, 230, 0.8));

        // --- Vitre arrière ---
        Rectangle winRear = new Rectangle(16, 4, 10, 10);
        winRear.setArcWidth(4);
        winRear.setArcHeight(4);
        winRear.setFill(Color.rgb(173, 216, 230, 0.8));

        // --- Phares avant ---
        Rectangle headlight = new Rectangle(59, 14, 5, 6);
        headlight.setArcWidth(2);
        headlight.setArcHeight(2);
        headlight.setFill(Color.LIGHTYELLOW);

        // --- Phares arrière ---
        Rectangle taillight = new Rectangle(0, 14, 5, 6);
        taillight.setArcWidth(2);
        taillight.setArcHeight(2);
        taillight.setFill(Color.RED);

        // --- Roues ---
        Ellipse wheelFL = wheel(12, 30);
        Ellipse wheelFR = wheel(50, 30);

        // --- Plaque d'immatriculation ---
        plateText = new Text(immatriculation);
        plateText.setFont(Font.font("Monospace", FontWeight.BOLD, 6));
        plateText.setFill(Color.WHITE);
        plateText.setX(8);
        plateText.setY(24);

        getChildren().addAll(body, roof, winFront, winRear, headlight, taillight,
                wheelFL, wheelFR, plateText);
    }

    private Ellipse wheel(double cx, double cy) {
        Ellipse w = new Ellipse(cx, cy, 7, 5);
        w.setFill(Color.rgb(30, 30, 30));
        w.setStroke(Color.GRAY);
        w.setStrokeWidth(1);
        return w;
    }
}
