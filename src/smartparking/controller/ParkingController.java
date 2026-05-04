package smartparking.controller;

import javafx.application.Platform;
import smartparking.model.Parking;
import smartparking.model.Transaction;
import smartparking.model.Voiture;
import smartparking.view.ParkingView;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParkingController implements Voiture.ParkingListener {

    private Parking parking;
    private ParkingView view;
    private ExecutorService executor;
    private int voitureCounter = 1;
    private final String[] COULEURS = {"#E74C3C", "#3498DB", "#2ECC71", "#F39C12",
                                        "#9B59B6", "#1ABC9C", "#E67E22", "#E91E63"};

    public ParkingController(Parking p) {
        this.parking = p;
        this.executor = Executors.newCachedThreadPool();
    }

    public void setView(ParkingView view) {
        this.view = view;
    }

    public void simulerEntreeAleatoire() {
        Random rand = new Random();
        String plaque = String.format("%c%c-%03d-%c%c",
                (char) (rand.nextInt(26) + 'A'), (char) (rand.nextInt(26) + 'A'),
                rand.nextInt(1000),
                (char) (rand.nextInt(26) + 'A'), (char) (rand.nextInt(26) + 'A'));

        int duree = rand.nextInt(5) + 1;
        String couleur = COULEURS[(voitureCounter - 1) % COULEURS.length];
        voitureCounter++;

        Voiture v = new Voiture(plaque, duree, parking, this);
        // Store color on voiture via the view's registry before submitting
        Platform.runLater(() -> view.enregistrerCouleur(plaque, couleur));
        executor.submit(v);
    }

    // ── Callbacks depuis les threads Voiture ───────────────────────────────

    @Override
    public void onVoitureEntreeFile(Voiture v) {
        Platform.runLater(() -> {
            view.voitureEntreeDansFile(v);
            view.ajouterLog(String.format("[FILE]   %s attend une place… (%dh simulée)",
                    v.getImmatriculation(), v.getDureeStationnementSimulee()));
        });
    }

    @Override
    public void onVoitureGaree(Voiture v, int numeroPlace) {
        Platform.runLater(() -> {
            view.voitureGaree(v, numeroPlace);
            view.ajouterLog(String.format("[ENTRÉE] %s → Place n°%d",
                    v.getImmatriculation(), numeroPlace));
        });
    }

    @Override
    public void onVoitureSortie(Voiture v, Transaction t) {
        Platform.runLater(() -> {
            view.voitureSortie(v, t);
            view.ajouterLog(t.toLogString());
        });
    }

    public Parking getParking() { return parking; }

    public void shutdown() {
        executor.shutdownNow();
    }
}
