package smartparking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String immatriculation;
    private LocalDateTime heureEntree;
    private LocalDateTime heureSortie;
    private double dureeSimuleeHeures;
    private double montantPaye;

    public Transaction(String id, LocalDateTime entree, LocalDateTime sortie, double dureeSimulee, double montant) {
        this.immatriculation = id;
        this.heureEntree = entree;
        this.heureSortie = sortie;
        this.dureeSimuleeHeures = dureeSimulee;
        this.montantPaye = montant;
    }

    /**
     * Format CSV pour la sauvegarde fichier.
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%.2f,%.2f",
                immatriculation,
                heureEntree.format(FORMATTER),
                heureSortie.format(FORMATTER),
                dureeSimuleeHeures,
                montantPaye);
    }

    /**
     * Format lisible pour l'affichage dans le log de la vue.
     */
    public String toLogString() {
        return String.format("[SORTIE] %s | Entrée: %s | Sortie: %s | Durée: %.1fh simulée | Montant: %.2f €",
                immatriculation,
                heureEntree.format(FORMATTER),
                heureSortie.format(FORMATTER),
                dureeSimuleeHeures,
                montantPaye);
    }

    public String getImmatriculation() { return immatriculation; }
    public double getMontantPaye()     { return montantPaye; }
    public double getDureeSimuleeHeures() { return dureeSimuleeHeures; }
    public LocalDateTime getHeureEntree() { return heureEntree; }
    public LocalDateTime getHeureSortie() { return heureSortie; }
}
