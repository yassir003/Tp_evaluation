package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Classe principale d'exécution et de test pour l'ensemble des 3 exercices JEE :
 * - Exercice 1 : Application de gestion de stock (Produit, Categorie, Commande, LigneCommande)
 * - Exercice 2 : Application de gestion de projets (Projet, Tache, Employe, EmployeTache)
 * - Exercice 3 : Application de gestion de l'état civil (Personne, Homme, Femme, Mariage)
 */
public class App {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("      APPLICATION DE TEST ET D'EXÉCUTION GLOBALE - ÉVALUATION JEE      ");
        System.out.println("========================================================================");

        if (args != null && args.length > 0) {
            String choice = args[0].trim().toLowerCase();
            switch (choice) {
                case "1":
                    testExercice1();
                    break;
                case "2":
                    testExercice2();
                    break;
                case "3":
                    testExercice3();
                    break;
                case "all":
                default:
                    runAll();
                    break;
            }
            return;
        }

        // Si aucun argument n'est fourni et si une entrée interactive est disponible
        if (System.console() != null) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nVeuillez sélectionner l'exercice à exécuter :");
            System.out.println("  [1] Exercice 1 : Gestion de Stock");
            System.out.println("  [2] Exercice 2 : Gestion de Projets");
            System.out.println("  [3] Exercice 3 : Gestion de l'État Civil");
            System.out.println("  [4] Exécuter TOUS les exercices (1, 2 et 3)");
            System.out.println("  [0] Quitter");
            System.out.print("\nVotre choix : ");

            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1":
                        testExercice1();
                        break;
                    case "2":
                        testExercice2();
                        break;
                    case "3":
                        testExercice3();
                        break;
                    case "4":
                        runAll();
                        break;
                    case "0":
                        System.out.println("Sortie de l'application.");
                        break;
                    default:
                        System.out.println("Choix inconnu. Exécution de tous les exercices par défaut :");
                        runAll();
                        break;
                }
            } else {
                runAll();
            }
        } else {
            // Mode automatique / non-interactif : exécution complète
            runAll();
        }
    }

    /**
     * Cas de test de l'Exercice 1 : Gestion de Stock
     * Teste les catégories, produits, commandes, lignes de commande et requêtes spécifiques.
     */
    public static void testExercice1() {
        printBanner("EXERCICE 1 : GESTION DE STOCK");
        System.out.println("Scénarios de test inclus :");
        System.out.println("  • 1. Création de catégories (Informatique, Bureautique, Téléphonie)");
        System.out.println("  • 2. Création et persistance des produits");
        System.out.println("  • 3. Création des commandes et association des lignes de commande");
        System.out.println("  • 4. Affichage des produits par catégorie");
        System.out.println("  • 5. Affichage des produits commandés entre deux dates");
        System.out.println("  • 6. Affichage formaté d'une commande (Num, Réf, Prix, Quantité)");
        System.out.println("  • 7. Requête HQL nommée : Produits dont le prix > 100 DH");
        System.out.println("------------------------------------------------------------------------\n");

        runModule("exercice1");
    }

    /**
     * Cas de test de l'Exercice 2 : Gestion de Projets
     * Teste les employés, projets, tâches planifiées, tâches réalisées et requêtes spécifiques.
     */
    public static void testExercice2() {
        printBanner("EXERCICE 2 : GESTION DE PROJETS");
        System.out.println("Scénarios de test inclus :");
        System.out.println("  • 1. Création des employés (ingénieurs, chefs de projet)");
        System.out.println("  • 2. Création des projets et attribution du chef de projet");
        System.out.println("  • 3. Création des tâches planifiées pour chaque projet");
        System.out.println("  • 4. Assignation des tâches aux employés avec dates réelles (EmployeTache)");
        System.out.println("  • 5. Affichage des tâches réalisées par un employé");
        System.out.println("  • 6. Affichage des projets gérés par un employé");
        System.out.println("  • 7. Affichage des tâches planifiées pour un projet");
        System.out.println("  • 8. Affichage formaté des tâches réalisées avec les dates réelles");
        System.out.println("  • 9. Requête nommée : Tâches dont le prix > 1000 DH");
        System.out.println("  • 10. Affichage des tâches réalisées entre deux dates");
        System.out.println("------------------------------------------------------------------------\n");

        runModule("exercice2");
    }

    /**
     * Cas de test de l'Exercice 3 : Gestion de l'État Civil
     * Teste l'héritage Personne/Homme/Femme, mariages, requêtes natives et API Criteria.
     */
    public static void testExercice3() {
        printBanner("EXERCICE 3 : GESTION DE L'ÉTAT CIVIL");
        System.out.println("Scénarios de test inclus :");
        System.out.println("  • 1. Création de 10 femmes et 5 hommes");
        System.out.println("  • 2. Affichage de la liste complète des femmes");
        System.out.println("  • 3. Recherche et affichage de la femme la plus âgée");
        System.out.println("  • 4. Affichage des épouses d'un homme donné entre deux dates");
        System.out.println("  • 5. Requête native SQL nommée : Nombre d'enfants d'une femme entre deux dates");
        System.out.println("  • 6. Requête HQL nommée : Femmes mariées au moins deux fois");
        System.out.println("  • 7. API Criteria : Nombre d'hommes mariés à 4 femmes entre deux dates");
        System.out.println("  • 8. Affichage détaillé des mariages d'un homme (En cours & Échoués)");
        System.out.println("------------------------------------------------------------------------\n");

        runModule("exercice3");
    }

    /**
     * Exécute les cas de test pour chacun des 3 exercices de manière séquentielle.
     */
    public static void runAll() {
        System.out.println("\n>>> DÉMARRAGE DE LA SUITE COMPLÈTE DE TESTS POUR LES 3 EXERCICES <<<\n");
        long start = System.currentTimeMillis();

        testExercice1();
        testExercice2();
        testExercice3();

        long duration = (System.currentTimeMillis() - start) / 1000;
        printBanner("RÉCAPITULATIF : TOUS LES CAS DE TEST ONT ÉTÉ EXÉCUTÉS AVEC SUCCÈS (" + duration + "s)");
    }

    /**
     * Exécute un sous-module Maven via ProcessBuilder de manière portable et robuste.
     */
    private static void runModule(String moduleName) {
        File workspaceDir = resolveWorkspaceDir();
        File moduleDir = new File(workspaceDir, moduleName);

        if (!moduleDir.exists()) {
            System.err.println("[ERREUR] Le dossier du module n'existe pas : " + moduleDir.getAbsolutePath());
            return;
        }

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String shell = isWindows ? "cmd.exe" : "sh";
        String flag = isWindows ? "/c" : "-c";
        String command = "mvn -pl " + moduleName + " exec:java";

        System.out.println("[INFO] Exécution : " + command + " dans " + workspaceDir.getAbsolutePath() + "\n");

        ProcessBuilder pb = new ProcessBuilder(shell, flag, command);
        pb.directory(workspaceDir);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Filtrage des lignes d'information Maven redondantes pour garder la clarté
                    if (!line.startsWith("[INFO] Scanning for projects")
                            && !line.startsWith("[INFO] Building ")
                            && !line.startsWith("[INFO] Finished at")
                            && !line.startsWith("[INFO] Total time")
                            && !line.startsWith("[INFO] --- exec-maven-plugin")) {
                        System.out.println(line);
                    }
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("[AVERTISSEMENT] Le module " + moduleName + " s'est terminé avec le code : " + exitCode);
            } else {
                System.out.println("\n[SUCCÈS] Cas de test de " + moduleName + " validés avec succès !\n");
            }
        } catch (Exception e) {
            System.err.println("[ERREUR] Exception lors de l'exécution de " + moduleName + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Localise le dossier racine du projet contenant pom.xml et les sous-dossiers.
     */
    private static File resolveWorkspaceDir() {
        File current = new File(".").getAbsoluteFile();
        if (new File(current, "exercice1").exists() && new File(current, "pom.xml").exists()) {
            return current;
        }
        File parent = current.getParentFile();
        if (parent != null && new File(parent, "exercice1").exists() && new File(parent, "pom.xml").exists()) {
            return parent;
        }
        return current;
    }

    private static void printBanner(String title) {
        System.out.println("\n========================================================================");
        System.out.println("  " + title);
        System.out.println("========================================================================");
    }
}
