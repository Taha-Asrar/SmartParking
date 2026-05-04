package smartparking.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.util.Duration;
import smartparking.controller.ParkingController;
import smartparking.model.Transaction;
import smartparking.model.Voiture;

import java.util.*;

public class ParkingView {

    // ── Layout constants ──────────────────────────────────────────────────
    private static final int COLS         = 5;
    private static final double SPOT_W    = 80;
    private static final double SPOT_H    = 55;
    private static final double SPOT_PAD  = 10;
    private static final double GRID_X    = 180;   // left edge of parking grid
    private static final double GRID_Y    = 100;
    private static final double ENTRY_X   = 20;    // entry road X
    private static final double BOOTH_X   = 700;   // payment booth X
    private static final double QUEUE_Y   = 520;   // waiting lane Y
    private static final double CANVAS_W  = 850;
    private static final double CANVAS_H  = 620;

    // ── State ─────────────────────────────────────────────────────────────
    private ParkingController controller;
    private Pane canvas;
    private TextArea logArea;
    private Label lblPlaces, lblWaiting, lblTotal;
    private Map<String, String>    colorRegistry = new HashMap<>();
    private Map<String, CarShape>  parkedCars    = new HashMap<>();
    private List<CarShape>         queueCars     = new ArrayList<>();
    private int totalTransactions  = 0;
    private double totalRevenue    = 0;

    public ParkingView(ParkingController controller) {
        this.controller = controller;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Scene construction
    // ─────────────────────────────────────────────────────────────────────
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f1a;");

        // ── Top bar ──────────────────────────────────────────────────────
        HBox topBar = buildTopBar();
        root.setTop(topBar);

        // ── Center canvas (animation zone) ───────────────────────────────
        canvas = new Pane();
        canvas.setPrefSize(CANVAS_W, CANVAS_H);
        drawStaticScene();
        root.setCenter(canvas);

        // ── Right panel (log + stats) ─────────────────────────────────────
        VBox rightPanel = buildRightPanel();
        root.setRight(rightPanel);

        return new Scene(root, 1100, 700);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Top bar
    // ─────────────────────────────────────────────────────────────────────
    private HBox buildTopBar() {
        HBox bar = new HBox(20);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #161625; -fx-border-color: #2a2a4a; -fx-border-width: 0 0 2 0;");

        Text title = new Text("🅿  SmartParking Simulation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setFill(Color.web("#00d4ff"));

        lblPlaces = badge("Places libres", "–", "#2ECC71");
        lblWaiting = badge("En attente", "0", "#F39C12");
        lblTotal = badge("Recettes", "0.00 €", "#9B59B6");

        Button btnAdd = new Button("+ Ajouter Voiture");
        btnAdd.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0f0f1a; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; "
                + "-fx-padding: 8 16 8 16;");
        btnAdd.setOnAction(e -> controller.simulerEntreeAleatoire());
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle("-fx-background-color: #00a8cc; -fx-text-fill: #0f0f1a; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16 8 16;"));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0f0f1a; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16 8 16;"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().addAll(title, spacer, lblPlaces, lblWaiting, lblTotal, btnAdd);
        return bar;
    }

    private Label badge(String label, String val, String hex) {
        Label l = new Label(label + ": " + val);
        l.setStyle("-fx-background-color: " + hex + "22; -fx-text-fill: " + hex + "; "
                + "-fx-border-color: " + hex + "; -fx-border-radius: 6; "
                + "-fx-background-radius: 6; -fx-padding: 5 12 5 12; -fx-font-weight: bold;");
        return l;
    }

    private void updateBadge(Label lbl, String label, String val, String hex) {
        lbl.setText(label + ": " + val);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Right panel (log)
    // ─────────────────────────────────────────────────────────────────────
    private VBox buildRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14));
        panel.setPrefWidth(250);
        panel.setStyle("-fx-background-color: #161625; -fx-border-color: #2a2a4a; -fx-border-width: 0 0 0 2;");

        Text logTitle = new Text("📋  Journal");
        logTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logTitle.setFill(Color.web("#00d4ff"));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-control-inner-background: #0f0f1a; -fx-text-fill: #c0c0d0; "
                + "-fx-font-family: 'Monospace'; -fx-font-size: 11; -fx-border-color: #2a2a4a;");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        Button btnClear = new Button("Effacer le log");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setStyle("-fx-background-color: #2a2a4a; -fx-text-fill: #c0c0d0; "
                + "-fx-background-radius: 6; -fx-cursor: hand;");
        btnClear.setOnAction(e -> logArea.clear());

        panel.getChildren().addAll(logTitle, logArea, btnClear);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Static scene (ground, spots, booth, road)
    // ─────────────────────────────────────────────────────────────────────
    private void drawStaticScene() {
        // Ground
        Rectangle ground = new Rectangle(0, 0, CANVAS_W, CANVAS_H);
        ground.setFill(Color.web("#1a1a2e"));
        canvas.getChildren().add(ground);

        // Entry road (vertical strip on the left)
        Rectangle entryRoad = new Rectangle(ENTRY_X, 0, 60, CANVAS_H - 120);
        entryRoad.setFill(Color.web("#252535"));
        canvas.getChildren().add(entryRoad);
        addRoadMarkings(ENTRY_X + 25, 0, CANVAS_H - 120);

        // Parking lot background
        double gridW = COLS * (SPOT_W + SPOT_PAD) + SPOT_PAD;
        int rows = (int) Math.ceil((double) controller.getParking().getCapaciteMax() / COLS);
        double gridH = rows * (SPOT_H + SPOT_PAD) + SPOT_PAD;
        Rectangle parkingLot = new Rectangle(GRID_X - 15, GRID_Y - 15, gridW + 20, gridH + 20);
        parkingLot.setFill(Color.web("#22223a"));
        parkingLot.setArcWidth(10); parkingLot.setArcHeight(10);
        parkingLot.setStroke(Color.web("#00d4ff44"));
        parkingLot.setStrokeWidth(1.5);
        canvas.getChildren().add(parkingLot);

        // Parking spots
        int capacity = controller.getParking().getCapaciteMax();
        for (int i = 0; i < capacity; i++) {
            int row = i / COLS, col = i % COLS;
            double x = GRID_X + col * (SPOT_W + SPOT_PAD);
            double y = GRID_Y + row * (SPOT_H + SPOT_PAD);
            drawParkingSpot(x, y, i + 1);
        }

        // Connecting road (horizontal, from entry to parking)
        Rectangle connector = new Rectangle(ENTRY_X + 60, GRID_Y + gridH / 2 - 25, GRID_X - ENTRY_X - 60, 50);
        connector.setFill(Color.web("#252535"));
        canvas.getChildren().add(connector);

        // Exit road (right side)
        Rectangle exitRoad = new Rectangle(GRID_X + gridW + 15, GRID_Y - 15, 60, gridH + 30);
        exitRoad.setFill(Color.web("#252535"));
        canvas.getChildren().add(exitRoad);
        addRoadMarkings(GRID_X + gridW + 45, GRID_Y - 15, gridH + 30);

        // Payment booth
        drawPaymentBooth(BOOTH_X, GRID_Y + gridH / 2 - 30);

        // Waiting queue lane (bottom)
        Rectangle queueLane = new Rectangle(20, QUEUE_Y - 15, BOOTH_X, 50);
        queueLane.setFill(Color.web("#1e1e30"));
        queueLane.setArcWidth(8); queueLane.setArcHeight(8);
        queueLane.setStroke(Color.web("#F39C1255"));
        queueLane.setStrokeWidth(1.5);
        canvas.getChildren().add(queueLane);

        Text queueLabel = new Text(30, QUEUE_Y + 50, "🚗  FILE D'ATTENTE");
        queueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        queueLabel.setFill(Color.web("#F39C12"));
        canvas.getChildren().add(queueLabel);

        // Labels
        addLabel("ENTRÉE ▶", ENTRY_X + 5, CANVAS_H - 110, "#2ECC71");
        addLabel("PARKING", GRID_X + gridW / 2 - 25, GRID_Y - 25, "#00d4ff");
    }

    private void drawParkingSpot(double x, double y, int number) {
        Rectangle spot = new Rectangle(x, y, SPOT_W, SPOT_H);
        spot.setFill(Color.web("#2a2a45"));
        spot.setStroke(Color.web("#ffffff22"));
        spot.setStrokeWidth(1);
        spot.setArcWidth(5); spot.setArcHeight(5);
        spot.setId("spot-" + number);
        canvas.getChildren().add(spot);

        Text numText = new Text(String.valueOf(number));
        numText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        numText.setFill(Color.web("#ffffff33"));
        numText.setX(x + SPOT_W / 2 - 5);
        numText.setY(y + SPOT_H / 2 + 5);
        canvas.getChildren().add(numText);
    }

    private void drawPaymentBooth(double x, double y) {
        Rectangle booth = new Rectangle(x, y, 70, 60);
        booth.setFill(Color.web("#2d2d50"));
        booth.setStroke(Color.web("#F39C12"));
        booth.setStrokeWidth(2);
        booth.setArcWidth(8); booth.setArcHeight(8);
        canvas.getChildren().add(booth);

        Text icon = new Text("💳");
        icon.setFont(Font.font(20));
        icon.setX(x + 22); icon.setY(y + 28);
        canvas.getChildren().add(icon);

        addLabel("GUICHET", x + 5, y + 50, "#F39C12");
    }

    private void addRoadMarkings(double cx, double startY, double height) {
        for (double y = startY + 15; y < startY + height; y += 30) {
            Line dash = new Line(cx, y, cx, y + 14);
            dash.setStroke(Color.web("#ffffff33"));
            dash.setStrokeWidth(2);
            dash.getStrokeDashArray().addAll(6.0, 6.0);
            canvas.getChildren().add(dash);
        }
    }

    private void addLabel(String txt, double x, double y, String hex) {
        Text t = new Text(x, y, txt);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        t.setFill(Color.web(hex));
        canvas.getChildren().add(t);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Spot coordinate helpers
    // ─────────────────────────────────────────────────────────────────────
    private double spotX(int numero) {
        int col = (numero - 1) % COLS;
        return GRID_X + col * (SPOT_W + SPOT_PAD) + 8;
    }

    private double spotY(int numero) {
        int row = (numero - 1) / COLS;
        return GRID_Y + row * (SPOT_H + SPOT_PAD) + 12;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Public API called by ParkingController (all on FX thread)
    // ─────────────────────────────────────────────────────────────────────

    /** Register color before the thread starts */
    public void enregistrerCouleur(String plaque, String hex) {
        colorRegistry.put(plaque, hex);
    }

    /** Called when a car joins the waiting queue */
    public void voitureEntreeDansFile(Voiture v) {
        String hex = colorRegistry.getOrDefault(v.getImmatriculation(), "#3498DB");
        CarShape car = new CarShape(v.getImmatriculation(), hex);

        // Position in queue lane
        double qx = 30 + queueCars.size() * (CarShape.CAR_W + 10);
        car.setLayoutX(qx);
        car.setLayoutY(QUEUE_Y - 5);
        queueCars.add(car);
        canvas.getChildren().add(car);

        updateStats();
    }

    /** Called when a car gets a spot – animate entry road → spot */
    public void voitureGaree(Voiture v, int numeroPlace) {
        // Remove from queue visuals
        if (!queueCars.isEmpty()) {
            CarShape queued = queueCars.remove(0);
            canvas.getChildren().remove(queued);
            reshuffleQueue();
        }

        String hex = colorRegistry.getOrDefault(v.getImmatriculation(), "#3498DB");
        CarShape car = new CarShape(v.getImmatriculation(), hex);
        car.setLayoutX(ENTRY_X + 2);
        car.setLayoutY(spotY(numeroPlace));
        canvas.getChildren().add(car);
        parkedCars.put(v.getImmatriculation(), car);

        // Animate: entry column → horizontal connector → parking spot
        double targetX = spotX(numeroPlace);
        double targetY = spotY(numeroPlace);

        // Step 1: slide right along connector (0.6s)
        TranslateTransition tt1 = new TranslateTransition(Duration.millis(600), car);
        tt1.setToX(targetX - ENTRY_X);

        // Step 2: already at correct Y (if same row), else slide down (0.4s)
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(400), car);
        tt2.setToY(0); // Y is already set at spotY

        // Highlight the spot green briefly
        tt1.setOnFinished(e -> {
            highlightSpot(numeroPlace, "#2ECC71");
            tt2.play();
        });
        tt1.play();

        updateStats();
    }

    /** Called when a car's time expires – animate spot → payment booth → exit */
    public void voitureSortie(Voiture v, Transaction t) {
        CarShape car = parkedCars.remove(v.getImmatriculation());
        if (car == null) return;

        double boothX = BOOTH_X;
        double currentAbsX = car.getLayoutX() + car.getTranslateX();
        double currentAbsY = car.getLayoutY() + car.getTranslateY();

        // Reset layout to absolute position
        car.setLayoutX(currentAbsX);
        car.setLayoutY(currentAbsY);
        car.setTranslateX(0);
        car.setTranslateY(0);

        int rows = (int) Math.ceil((double) controller.getParking().getCapaciteMax() / COLS);
        double boothY = GRID_Y + rows * (SPOT_H + SPOT_PAD) / 2.0 - 10;

        // Step 1: slide right to exit road
        int exitRoadX = (int)(GRID_X + COLS * (SPOT_W + SPOT_PAD) + 15);
        TranslateTransition toExit = new TranslateTransition(Duration.millis(500), car);
        toExit.setToX(exitRoadX - currentAbsX);

        // Step 2: slide down to booth level
        TranslateTransition toBooth = new TranslateTransition(Duration.millis(400), car);
        toBooth.setToY(boothY - currentAbsY);

        // Step 3: slide right to booth
        TranslateTransition pay = new TranslateTransition(Duration.millis(400), car);
        pay.setToX(boothX - currentAbsX);

        toExit.setOnFinished(e -> toBooth.play());
        toBooth.setOnFinished(e -> {
            pay.play();
            showPaymentPopup(car, t);
        });
        pay.setOnFinished(e -> {
            // Final exit off-screen
            TranslateTransition leave = new TranslateTransition(Duration.millis(500), car);
            leave.setToX(CANVAS_W + 80 - currentAbsX);
            leave.setOnFinished(ev -> canvas.getChildren().remove(car));
            leave.play();

            totalTransactions++;
            totalRevenue += t.getMontantPaye();
            updateBadge(lblTotal, "Recettes",
                    String.format("%.2f €", totalRevenue), "#9B59B6");
        });

        toExit.play();
        updateStats();
    }

    /** Show a floating payment label near the booth */
    private void showPaymentPopup(CarShape car, Transaction t) {
        javafx.scene.control.Label popup = new javafx.scene.control.Label(
                String.format("💳  %s\n%.2f €", t.getImmatriculation(), t.getMontantPaye()));
        popup.setStyle("-fx-background-color: #2d2d50; -fx-text-fill: #F39C12; "
                + "-fx-font-weight: bold; -fx-padding: 8 12 8 12; "
                + "-fx-background-radius: 8; -fx-border-color: #F39C12; -fx-border-radius: 8;");
        popup.setLayoutX(BOOTH_X - 10);
        popup.setLayoutY(GRID_Y - 60);
        canvas.getChildren().add(popup);

        FadeTransition ft = new FadeTransition(Duration.millis(2500), popup);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setDelay(Duration.millis(1200));
        ft.setOnFinished(e -> canvas.getChildren().remove(popup));
        ft.play();
    }

    private void highlightSpot(int numero, String hex) {
        canvas.lookupAll("#spot-" + numero).forEach(node -> {
            if (node instanceof Rectangle) {
                Rectangle r = (Rectangle) node;
                r.setFill(Color.web(hex + "55"));
                // Fade back after 1s
                PauseTransition pt = new PauseTransition(Duration.millis(1000));
                pt.setOnFinished(e -> r.setFill(Color.web("#2a2a45")));
                pt.play();
            }
        });
    }

    private void reshuffleQueue() {
        for (int i = 0; i < queueCars.size(); i++) {
            CarShape c = queueCars.get(i);
            double targetX = 30 + i * (CarShape.CAR_W + 10);
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), c);
            tt.setToX(targetX - c.getLayoutX());
            tt.play();
        }
    }

    // ── Log ───────────────────────────────────────────────────────────────
    public void ajouterLog(String message) {
        logArea.appendText(message + "\n");
    }

    // ── Stats ─────────────────────────────────────────────────────────────
    private void updateStats() {
        int places = controller.getParking().getPlacesDisponibles();
        int waiting = controller.getParking().getFileAttente().size();
        updateBadge(lblPlaces,   "Places libres",  String.valueOf(places), "#2ECC71");
        updateBadge(lblWaiting,  "En attente",     String.valueOf(waiting), "#F39C12");
    }
}
