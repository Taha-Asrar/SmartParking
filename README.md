# SmartParking - Simulation POO Java

Ce mini-projet est une simulation interactive d'un parking public payant, développé en Java avec JavaFX pour l'interface graphique. Le projet met en pratique les concepts avancés de la Programmation Orientée Objet (POO) tels que le multithreading, la synchronisation, les collections et la manipulation de fichiers.

## 🌟 Fonctionnalités

- **Simulation en Temps Réel** : Les voitures entrent, stationnent et sortent de manière autonome.
- **Multithreading** : Chaque voiture est gérée par son propre thread (implémentation de `Runnable`), permettant des entrées et sorties simultanées.
- **Synchronisation** : Gestion stricte de la file d'attente et des places de parking avec `wait()` et `notifyAll()` pour éviter les collisions et les conditions de concurrence.
- **Interface Graphique Moderne (JavaFX)** :
  - Thème sombre et épuré.
  - Animation fluide des voitures (entrée, stationnement, sortie vers le guichet).
  - Affichage en direct de la file d'attente extérieure.
  - Tableau de bord avec statistiques (places libres, voitures en attente, recettes totales).
- **Tarification et Historique** :
  - Calcul du tarif basé sur le temps passé (4 secondes réelles = 1 heure simulée).
  - Enregistrement automatique des transactions dans un fichier `log.csv` persistant.

## 🏗️ Architecture

Le projet respecte strictement le patron de conception **MVC (Modèle-Vue-Contrôleur)** avec une séparation claire des responsabilités :
- **Modèle** (`smartparking.model`) : Les entités de base (`Voiture`, `Parking`, `Transaction`).
- **Logique Métier** (`smartparking.metier`) : Les services (`Tarificateur`, `GestionnaireFichier`).
- **Vue** (`smartparking.view`) : L'interface et les animations (`ParkingView`, `CarShape`).
- **Contrôleur** (`smartparking.controller`) : L'orchestrateur (`ParkingController`).

## 🚀 Comment exécuter le projet ?

Ce projet est un projet Java standard (sans gestionnaire de dépendances comme Maven ou Gradle).

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/VOTRE-NOM-UTILISATEUR/SmartParking.git
   ```

2. **Ouvrir avec IntelliJ IDEA (recommandé) :**
   - Ouvrez le dossier `SmartParking` dans votre IDE.
   - Assurez-vous que le dossier `src` est marqué comme **"Sources Root"** (Clic droit sur `src` -> Mark Directory as -> Sources Root).

3. **Configurer JavaFX :**
   - Si vous utilisez une version de Java supérieure à Java 8, vous devez ajouter la bibliothèque JavaFX.
   - Allez dans `File > Project Structure > Libraries` et ajoutez les `.jar` de votre JavaFX SDK.
   - Dans votre configuration de lancement (`Run Configuration` de la classe `Main`), ajoutez ces **VM Options** (en remplaçant le chemin) :
     ```text
     --module-path "\chemin\vers\votre\javafx-sdk\lib" --add-modules javafx.controls,javafx.graphics
     ```

4. **Lancer l'application :**
   - Exécutez la classe `Main` située dans `src/smartparking/Main.java`.

## 📸 Aperçu de l'application
*(Vous pouvez ajouter une capture d'écran ici après l'avoir push sur GitHub en modifiant ce fichier et en ajoutant l'image dans le dossier)*

---
*Projet réalisé dans le cadre d'un module de Programmation Orientée Objet (POO) Avancée.*
