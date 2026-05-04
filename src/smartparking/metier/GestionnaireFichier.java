package smartparking.metier;

import smartparking.model.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class GestionnaireFichier {

    private static final String FICHIER_TRANSACTIONS = "log.csv";
    private static final String CSV_HEADER =
            "Immatriculation,Heure Entree,Heure Sortie,Duree Simulee (h),Montant (EUR)";

    /**
     * Sauvegarde une transaction dans le fichier CSV.
     * Crée le fichier avec son en-tête s'il n'existe pas encore.
     */
    public static synchronized void sauvegarderTransaction(Transaction t) {
        boolean exists = Files.exists(Paths.get(FICHIER_TRANSACTIONS));
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHIER_TRANSACTIONS, true))) {
            if (!exists) {
                pw.println(CSV_HEADER);
            }
            pw.println(t.toCSV());
        } catch (IOException e) {
            System.err.println("[GestionnaireFichier] Erreur de sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Lit toutes les lignes du fichier d'historique.
     * Retourne une liste vide si le fichier n'existe pas.
     */
    public static List<String> lireHistorique() {
        try {
            if (Files.exists(Paths.get(FICHIER_TRANSACTIONS))) {
                return Files.readAllLines(Paths.get(FICHIER_TRANSACTIONS));
            }
        } catch (IOException e) {
            System.err.println("[GestionnaireFichier] Erreur de lecture : " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
