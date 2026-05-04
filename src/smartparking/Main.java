package smartparking;

import javafx.application.Application;
import javafx.stage.Stage;
import smartparking.controller.ParkingController;
import smartparking.model.Parking;
import smartparking.view.ParkingView;

public class Main extends Application {

    private ParkingController controller;

    @Override
    public void start(Stage primaryStage) {
        // 1. Créer le modèle
        Parking parking = new Parking(10);

        // 2. Créer le contrôleur (sans vue pour l'instant)
        controller = new ParkingController(parking);

        // 3. Créer la vue en lui passant le contrôleur
        ParkingView view = new ParkingView(controller);

        // 4. Injecter la vue dans le contrôleur (liaison bidirectionnelle)
        controller.setView(view);

        // 5. Configurer et afficher la fenêtre
        primaryStage.setTitle("SmartParking — Simulation POO Java");
        primaryStage.setScene(view.createScene());
        primaryStage.setResizable(false);

        // 6. Arrêt propre des threads à la fermeture
        primaryStage.setOnCloseRequest(e -> controller.shutdown());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
