package smartparking.model;

import smartparking.metier.Tarificateur;

import java.time.LocalDateTime;
import java.util.*;

public class Parking {
    private int capaciteMax;
    private Map<Integer, Voiture> vehiculesPresents; // numéro de place -> voiture
    private Queue<Voiture> fileAttente;

    public Parking(int capaciteMax) {
        this.capaciteMax = capaciteMax;
        this.vehiculesPresents = new LinkedHashMap<>();
        this.fileAttente = new LinkedList<>();
    }

    /**
     * Retourne le numéro de la place attribuée. Bloque (wait) si le parking est plein.
     */
    public synchronized int entrer(Voiture v) throws InterruptedException {
        fileAttente.add(v);

        while (vehiculesPresents.size() >= capaciteMax || !fileAttente.peek().equals(v)) {
            wait();
        }

        fileAttente.poll();
        int numeroPlace = trouverPlaceLibre();
        vehiculesPresents.put(numeroPlace, v);
        return numeroPlace;
    }

    public synchronized Transaction sortir(Voiture v) {
        int numeroPlace = -1;
        for (Map.Entry<Integer, Voiture> entry : vehiculesPresents.entrySet()) {
            if (entry.getValue().equals(v)) {
                numeroPlace = entry.getKey();
                break;
            }
        }
        if (numeroPlace != -1) {
            vehiculesPresents.remove(numeroPlace);
        }

        LocalDateTime sortie = LocalDateTime.now();
        double dureeSimulee = Tarificateur.calculerDureeSimulee(v.getHeureEntreeReelle(), sortie);
        double montant = Tarificateur.calculerMontant(v.getHeureEntreeReelle(), sortie);

        Transaction t = new Transaction(v.getImmatriculation(), v.getHeureEntreeReelle(), sortie, dureeSimulee, montant);

        notifyAll();
        return t;
    }

    private int trouverPlaceLibre() {
        for (int i = 1; i <= capaciteMax; i++) {
            if (!vehiculesPresents.containsKey(i)) return i;
        }
        return -1;
    }

    public synchronized int getPlacesDisponibles()        { return capaciteMax - vehiculesPresents.size(); }
    public synchronized Map<Integer, Voiture> getVehiculesPresents() { return new LinkedHashMap<>(vehiculesPresents); }
    public synchronized Queue<Voiture> getFileAttente()   { return new LinkedList<>(fileAttente); }
    public int getCapaciteMax()                            { return capaciteMax; }
}
