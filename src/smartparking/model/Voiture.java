package smartparking.model;

import smartparking.metier.GestionnaireFichier;
import smartparking.metier.Tarificateur;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Voiture implements Runnable {
    private String immatriculation;
    private LocalDateTime heureEntreeReelle;
    private int dureeStationnementSimulee; // en heures simulées
    private Parking parking;
    private ParkingListener listener;

    public interface ParkingListener {
        void onVoitureEntreeFile(Voiture v);
        void onVoitureGaree(Voiture v, int numerPlace);
        void onVoitureSortie(Voiture v, Transaction t);
    }

    public Voiture(String immatriculation, int dureeSimulee, Parking p, ParkingListener listener) {
        this.immatriculation = immatriculation;
        this.dureeStationnementSimulee = dureeSimulee;
        this.parking = p;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            // Notifier : voiture dans la file d'attente
            if (listener != null) listener.onVoitureEntreeFile(this);

            int numeroPlace = parking.entrer(this);
            this.heureEntreeReelle = LocalDateTime.now();

            // Notifier : voiture garée à sa place
            if (listener != null) listener.onVoitureGaree(this, numeroPlace);

            // Attente simulée : dureeSimulee heures × 4 secondes / heure
            long tempsSommeilMillis = (long) dureeStationnementSimulee
                    * Tarificateur.SECONDES_POUR_UNE_HEURE * 1000;
            Thread.sleep(tempsSommeilMillis);

            Transaction t = parking.sortir(this);
            GestionnaireFichier.sauvegarderTransaction(t);

            // Notifier : voiture sortie avec la transaction
            if (listener != null) listener.onVoitureSortie(this, t);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getImmatriculation()        { return immatriculation; }
    public LocalDateTime getHeureEntreeReelle() { return heureEntreeReelle; }
    public int getDureeStationnementSimulee()  { return dureeStationnementSimulee; }

    @Override
    public String toString() { return immatriculation; }
}
