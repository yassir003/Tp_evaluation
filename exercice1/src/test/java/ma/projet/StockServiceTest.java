package ma.projet;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.service.CategorieService;
import ma.projet.service.CommandeService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.ProduitService;
import ma.projet.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class StockServiceTest {

    private static CategorieService categorieService;
    private static ProduitService produitService;
    private static CommandeService commandeService;
    private static LigneCommandeService ligneCommandeService;

    @BeforeClass
    public static void setUp() {
        categorieService = new CategorieService();
        produitService = new ProduitService();
        commandeService = new CommandeService();
        ligneCommandeService = new LigneCommandeService();
    }

    @AfterClass
    public static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    public void testGestionStockComplete() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // 1. Test création Catégorie
        Categorie cat = new Categorie("TEST_CAT", "Test Catégorie");
        assertTrue("La catégorie doit être créée", categorieService.create(cat));
        assertNotNull(categorieService.findById(cat.getId()));

        // 2. Test création Produits
        Produit p1 = new Produit("ES12", 120.0f, cat);
        Produit p2 = new Produit("ZR85", 100.0f, cat);
        Produit p3 = new Produit("EE85", 200.0f, cat);
        assertTrue(produitService.create(p1));
        assertTrue(produitService.create(p2));
        assertTrue(produitService.create(p3));

        // 3. Test recherche par catégorie
        List<Produit> parCat = produitService.findByCategorie(cat);
        assertEquals(3, parCat.size());

        // 4. Test création Commande & Lignes de commande
        Date date14Mars = sdf.parse("14/03/2013");
        Commande cmd = new Commande(date14Mars);
        assertTrue(commandeService.create(cmd));

        LigneCommandeProduit l1 = new LigneCommandeProduit(p1, cmd, 7);
        LigneCommandeProduit l2 = new LigneCommandeProduit(p2, cmd, 14);
        LigneCommandeProduit l3 = new LigneCommandeProduit(p3, cmd, 5);
        assertTrue(ligneCommandeService.create(l1));
        assertTrue(ligneCommandeService.create(l2));
        assertTrue(ligneCommandeService.create(l3));

        // 5. Test recherche des lignes de commande par commande
        List<LigneCommandeProduit> lignes = ligneCommandeService.findByCommandeId(cmd.getId());
        assertEquals(3, lignes.size());

        // 6. Test recherche des produits commandés entre deux dates
        Date debut = sdf.parse("01/03/2013");
        Date fin = sdf.parse("31/03/2013");
        List<Produit> produitsDates = produitService.findProduitsCommandesEntreDates(debut, fin);
        assertEquals(3, produitsDates.size());

        // 7. Test requête nommée (prix > 100 DH)
        List<Produit> prixSup100 = produitService.findPrixSuperieurA100();
        assertFalse("Il doit y avoir des produits avec prix > 100", prixSup100.isEmpty());
        for (Produit p : prixSup100) {
            assertTrue("Le prix doit être strictement supérieur à 100 DH", p.getPrix() > 100.0f);
        }
    }
}
