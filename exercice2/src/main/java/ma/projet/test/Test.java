package ma.projet.test;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.EmployeService;
import ma.projet.service.EmployeTacheService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import ma.projet.util.HibernateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   TEST APPLICATION DE GESTION DE PROJETS - EXERCICE 2");
        System.out.println("=================================================================\n");

        EmployeService employeService = new EmployeService();
        ProjetService projetService = new ProjetService();
        TacheService tacheService = new TacheService();
        EmployeTacheService employeTacheService = new EmployeTacheService();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // 1. Création des employés
            System.out.println(">>> 1. Création des employés");
            Employe empChef = new Employe("Alami", "Ahmed", "0661122334");
            Employe empDev1 = new Employe("Bennani", "Karim", "0662233445");
            Employe empDev2 = new Employe("Idrissi", "Sara", "0663344556");

            employeService.create(empChef);
            employeService.create(empDev1);
            employeService.create(empDev2);
            System.out.println("Employés créés avec succès.\n");

            // 2. Création des projets
            System.out.println(">>> 2. Création des projets");
            Date dateDebutProj = sdf.parse("14/01/2013"); // 14 Janvier 2013
            Date dateFinProj = sdf.parse("31/12/2013");
            Projet projetStock = new Projet("Gestion de stock", dateDebutProj, dateFinProj, empChef);
            projetService.create(projetStock);

            Projet projetRH = new Projet("Gestion RH", sdf.parse("01/06/2013"), sdf.parse("31/12/2013"), empChef);
            projetService.create(projetRH);
            System.out.println("Projets créés avec succès.\n");

            // 3. Création des tâches planifiées
            System.out.println(">>> 3. Création des tâches");
            Tache t1 = new Tache("Analyse", sdf.parse("01/02/2013"), sdf.parse("28/02/2013"), 1500.0, projetStock);
            Tache t2 = new Tache("Conception", sdf.parse("01/03/2013"), sdf.parse("31/03/2013"), 2000.0, projetStock);
            Tache t3 = new Tache("Développement", sdf.parse("01/04/2013"), sdf.parse("30/04/2013"), 4500.0, projetStock);
            Tache t4 = new Tache("Documentation", sdf.parse("01/05/2013"), sdf.parse("15/05/2013"), 800.0, projetStock);

            tacheService.create(t1);
            tacheService.create(t2);
            tacheService.create(t3);
            tacheService.create(t4);
            System.out.println("Tâches créées avec succès.\n");

            // 4. Affectation et réalisation des tâches par les employés avec dates réelles
            System.out.println(">>> 4. Affectation et réalisation des tâches");
            // Analyse : 10/02/2013 au 20/02/2013
            EmployeTache et1 = new EmployeTache(empDev1, t1, sdf.parse("10/02/2013"), sdf.parse("20/02/2013"));
            // Conception : 10/03/2013 au 15/03/2013
            EmployeTache et2 = new EmployeTache(empDev2, t2, sdf.parse("10/03/2013"), sdf.parse("15/03/2013"));
            // Développement : 10/04/2013 au 25/04/2013
            EmployeTache et3 = new EmployeTache(empDev1, t3, sdf.parse("10/04/2013"), sdf.parse("25/04/2013"));

            employeTacheService.create(et1);
            employeTacheService.create(et2);
            employeTacheService.create(et3);
            System.out.println("Affectations créées avec succès.\n");

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 1 : Liste des projets gérés par un employé");
            System.out.println("-----------------------------------------------------------------");
            employeService.afficherProjetsGeres(empChef);
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 2 : Liste des tâches réalisées par un employé");
            System.out.println("-----------------------------------------------------------------");
            employeService.afficherTachesRealisees(empDev1);
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 3 : Liste des tâches planifiées pour un projet");
            System.out.println("-----------------------------------------------------------------");
            projetService.afficherTachesPlanifiees(projetStock);
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 4 : Liste des tâches réalisées avec les dates réelles (Format attendu)");
            System.out.println("-----------------------------------------------------------------");
            projetService.afficherTachesRealisees(projetStock);
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 5 : Tâches dont le prix est supérieur à 1000 DH (Requête nommée)");
            System.out.println("-----------------------------------------------------------------");
            tacheService.afficherTachesPrixSuperieurA1000();
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 6 : Tâches réalisées entre deux dates (01/02/2013 et 31/03/2013)");
            System.out.println("-----------------------------------------------------------------");
            tacheService.afficherTachesRealiseesEntreDates(sdf.parse("01/02/2013"), sdf.parse("31/03/2013"));
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
