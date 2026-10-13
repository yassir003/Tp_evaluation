package ma.projet.test;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.service.CategorieService;
import ma.projet.service.CommandeService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.ProduitService;
import ma.projet.util.HibernateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   TEST APPLICATION DE GESTION DE STOCK - EXERCICE 1");
        System.out.println("=================================================================\n");

        // 1. Initialisation des services
        CategorieService categorieService = new CategorieService();
        ProduitService produitService = new ProduitService();
        CommandeService commandeService = new CommandeService();
        LigneCommandeService ligneCommandeService = new LigneCommandeService();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // 2. Création des catégories
            System.out.println(">>> 1. Création des catégories");
            Categorie catInfo = new Categorie("INFO", "Matériel Informatique");
            Categorie catBureautique = new Categorie("BUR", "Bureautique");
            categorieService.create(catInfo);
            categorieService.create(catBureautique);
            System.out.println("Catégories créées avec succès.\n");

            // 3. Création des produits (ES12: 120 DH, ZR85: 100 DH, EE85: 200 DH, etc.)
            System.out.println(">>> 2. Création des produits");
            Produit p1 = new Produit("ES12", 120.0f, catInfo);
            Produit p2 = new Produit("ZR85", 100.0f, catInfo);
            Produit p3 = new Produit("EE85", 200.0f, catInfo);
            Produit p4 = new Produit("MS10", 45.0f, catInfo);
            Produit p5 = new Produit("CL05", 80.0f, catBureautique);

            produitService.create(p1);
            produitService.create(p2);
            produitService.create(p3);
            produitService.create(p4);
            produitService.create(p5);
            System.out.println("Produits créés avec succès.\n");

            // 4. Création des commandes
            System.out.println(">>> 3. Création des commandes");
            Date dateCmd1 = sdf.parse("14/03/2013"); // 14 Mars 2013
            Commande cmd1 = new Commande(dateCmd1);
            commandeService.create(cmd1);

            Date dateCmd2 = sdf.parse("25/04/2013");
            Commande cmd2 = new Commande(dateCmd2);
            commandeService.create(cmd2);
            System.out.println("Commandes créées avec succès.\n");

            // 5. Création des lignes de commande pour la commande 1
            System.out.println(">>> 4. Ajout des lignes de commande");
            // ES12 -> 120 DH, Quantité 7
            LigneCommandeProduit l1 = new LigneCommandeProduit(p1, cmd1, 7);
            // ZR85 -> 100 DH, Quantité 14
            LigneCommandeProduit l2 = new LigneCommandeProduit(p2, cmd1, 14);
            // EE85 -> 200 DH, Quantité 5
            LigneCommandeProduit l3 = new LigneCommandeProduit(p3, cmd1, 5);

            ligneCommandeService.create(l1);
            ligneCommandeService.create(l2);
            ligneCommandeService.create(l3);

            // Pour la deuxième commande (pour tester le filtrage par dates)
            LigneCommandeProduit l4 = new LigneCommandeProduit(p4, cmd2, 2);
            ligneCommandeService.create(l4);
            System.out.println("Lignes de commande créées avec succès.\n");

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 1 : Liste des produits par catégorie");
            System.out.println("-----------------------------------------------------------------");
            produitService.afficherProduitsParCategorie(catInfo);
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 2 : Produits commandés entre deux dates (01/03/2013 et 31/03/2013)");
            System.out.println("-----------------------------------------------------------------");
            Date debut = sdf.parse("01/03/2013");
            Date fin = sdf.parse("31/03/2013");
            produitService.afficherProduitsCommandesEntreDates(debut, fin);
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 3 : Produits commandés dans une commande donnée (Format exact)");
            System.out.println("-----------------------------------------------------------------");
            produitService.afficherProduitsParCommande(cmd1);
            System.out.println();

            System.out.println("-----------------------------------------------------------------");
            System.out.println("TEST 4 : Produits dont le prix est supérieur à 100 DH (Requête nommée)");
            System.out.println("-----------------------------------------------------------------");
            produitService.afficherProduitsPrixSuperieurA100();
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
