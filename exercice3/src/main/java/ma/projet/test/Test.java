package ma.projet.test;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.MariageService;
import ma.projet.util.HibernateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   TEST APPLICATION GESTION DE L'ÉTAT CIVIL - EXERCICE 3");
        System.out.println("=================================================================\n");

        HommeService hommeService = new HommeService();
        FemmeService femmeService = new FemmeService();
        MariageService mariageService = new MariageService();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // 1. Création de 10 femmes
            System.out.println(">>> 1. Création de 10 femmes");
            Femme f1 = new Femme("RAMI", "Salima", "0611000001", "Rabat", sdf.parse("15/04/1968"));
            Femme f2 = new Femme("ALI", "Amal", "0611000002", "Casablanca", sdf.parse("20/06/1972"));
            Femme f3 = new Femme("ALAOUI", "Wafa", "0611000003", "Fès", sdf.parse("10/11/1975"));
            Femme f4 = new Femme("ALAMI", "Karima", "0611000004", "Marrakech", sdf.parse("05/01/1960")); // La plus âgée
            Femme f5 = new Femme("BENANI", "Naima", "0611000005", "Tanger", sdf.parse("25/08/1978"));
            Femme f6 = new Femme("IDRISSI", "Latifa", "0611000006", "Agadir", sdf.parse("14/02/1982"));
            Femme f7 = new Femme("ZAHRA", "Fatima", "0611000007", "Meknès", sdf.parse("30/09/1985"));
            Femme f8 = new Femme("CHRAIBI", "Houda", "0611000008", "Oujda", sdf.parse("18/12/1988"));
            Femme f9 = new Femme("TAHIRI", "Meriem", "0611000009", "Kénitra", sdf.parse("22/03/1990"));
            Femme f10 = new Femme("KADIRI", "Souad", "0611000010", "Tétouan", sdf.parse("11/07/1992"));

            femmeService.create(f1);
            femmeService.create(f2);
            femmeService.create(f3);
            femmeService.create(f4);
            femmeService.create(f5);
            femmeService.create(f6);
            femmeService.create(f7);
            femmeService.create(f8);
            femmeService.create(f9);
            femmeService.create(f10);
            System.out.println("10 femmes créées avec succès.\n");

            // 2. Création de 5 hommes
            System.out.println(">>> 2. Création de 5 hommes");
            Homme h1 = new Homme("SAFI", "Said", "0622000001", "Casablanca", sdf.parse("01/01/1965"));
            Homme h2 = new Homme("TAZI", "Karim", "0622000002", "Rabat", sdf.parse("10/05/1972"));
            Homme h3 = new Homme("FASSI", "Omar", "0622000003", "Fès", sdf.parse("12/11/1968"));
            Homme h4 = new Homme("AMINE", "Hassan", "0622000004", "Marrakech", sdf.parse("20/07/1975"));
            Homme h5 = new Homme("BERADA", "Youssef", "0622000005", "Tanger", sdf.parse("15/03/1980"));

            hommeService.create(h1);
            hommeService.create(h2);
            hommeService.create(h3);
            hommeService.create(h4);
            hommeService.create(h5);
            System.out.println("5 hommes créés avec succès.\n");

            // 3. Création des mariages pour SAFI SAID
            System.out.println(">>> 3. Création des mariages");
            // Mariage échoué avec KARIMA ALAMI : 03/09/1989 au 03/09/1990, 0 enfant
            Mariage m1 = new Mariage(h1, f4, sdf.parse("03/09/1989"), sdf.parse("03/09/1990"), 0);
            // Mariage en cours avec SALIMA RAMI : début 03/09/1990, 4 enfants
            Mariage m2 = new Mariage(h1, f1, sdf.parse("03/09/1990"), null, 4);
            // Mariage en cours avec AMAL ALI : début 03/09/1995, 2 enfants
            Mariage m3 = new Mariage(h1, f2, sdf.parse("03/09/1995"), null, 2);
            // Mariage en cours avec WAFA ALAOUI : début 04/11/2000, 3 enfants
            Mariage m4 = new Mariage(h1, f3, sdf.parse("04/11/2000"), null, 3);
            // 4ème mariage en cours avec NAIMA BENANI : début 10/01/2002, 1 enfant (pour test 4 épouses)
            Mariage m5 = new Mariage(h1, f5, sdf.parse("10/01/2002"), null, 1);

            mariageService.create(m1);
            mariageService.create(m2);
            mariageService.create(m3);
            mariageService.create(m4);
            mariageService.create(m5);

            // Mariages pour tester les femmes mariées au moins deux fois :
            // Karima Alami se remarie avec TAZI Karim le 15/06/1993
            Mariage m6 = new Mariage(h2, f4, sdf.parse("15/06/1993"), null, 2);
            // Amal Ali a eu un premier mariage avec FASSI Omar du 01/01/1991 au 01/01/1993
            Mariage m7 = new Mariage(h3, f2, sdf.parse("01/01/1991"), sdf.parse("01/01/1993"), 1);

            mariageService.create(m6);
            mariageService.create(m7);
            System.out.println("Mariages créés avec succès.\n");

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 1 : Afficher la liste des femmes");
            System.out.println("-----------------------------------------------------------------");
            List<Femme> femmes = femmeService.findAll();
            for (Femme f : femmes) {
                System.out.println(" - " + f.getId() + " : " + f.getNom() + " " + f.getPrenom() +
                        " (Née le : " + sdf.format(f.getDateNaissance()) + ")");
            }
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 2 : Afficher la femme la plus âgée");
            System.out.println("-----------------------------------------------------------------");
            femmeService.afficherFemmePlusAgee();
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 3 : Afficher les épouses d'un homme donné (SAFI SAID entre 1990 et 2005)");
            System.out.println("-----------------------------------------------------------------");
            hommeService.afficherEpousesEntreDates(h1, sdf.parse("01/01/1990"), sdf.parse("31/12/2005"));
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 4 : Afficher le nombre d'enfants d'une femme entre deux dates (SALIMA RAMI)");
            System.out.println("-----------------------------------------------------------------");
            femmeService.afficherNombreEnfantsEntreDates(f1, sdf.parse("01/01/1985"), sdf.parse("31/12/2000"));
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 5 : Afficher les femmes mariées deux fois ou plus (Requête nommée)");
            System.out.println("-----------------------------------------------------------------");
            femmeService.afficherFemmesMarieesAuMoinsDeuxFois();
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 6 : Afficher les hommes mariés à quatre femmes entre deux dates (API Criteria)");
            System.out.println("-----------------------------------------------------------------");
            hommeService.afficherNombreHommesMariesQuatreFemmesEntreDates(sdf.parse("01/01/2000"), sdf.parse("31/12/2010"));
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 7 : Afficher les mariages d'un homme avec tous les détails (Format exact)");
            System.out.println("-----------------------------------------------------------------");
            hommeService.afficherMariagesParHomme(h1);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
