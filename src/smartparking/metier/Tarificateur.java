package smartparking.metier;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Tarificateur {
    public static final double TARIF_HORAIRE = 2.5;
    public static final int SECONDES_POUR_UNE_HEURE = 4;

    public static double calculerDureeSimulee(LocalDateTime entree, LocalDateTime sortie) {
        long secondesReelles = ChronoUnit.SECONDS.between(entree, sortie);
        return (double) secondesReelles / SECONDES_POUR_UNE_HEURE;
    }

    public static double calculerMontant(LocalDateTime entree, LocalDateTime sortie) {
        double duree = calculerDureeSimulee(entree, sortie);
        return duree * TARIF_HORAIRE;
    }
}
