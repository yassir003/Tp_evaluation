package ma.projet;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.EmployeService;
import ma.projet.service.EmployeTacheService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import ma.projet.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ProjetServiceTest {

    private static EmployeService employeService;
    private static ProjetService projetService;
    private static TacheService tacheService;
    private static EmployeTacheService employeTacheService;

    @BeforeClass
    public static void setUp() {
        employeService = new EmployeService();
        projetService = new ProjetService();
        tacheService = new TacheService();
        employeTacheService = new EmployeTacheService();
    }

    @AfterClass
    public static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    public void testGestionProjetsComplete() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // 1. Création Employé
        Employe chef = new Employe("Alami", "Ahmed", "0611223344");
        Employe dev = new Employe("Bennani", "Karim", "0622334455");
        assertTrue(employeService.create(chef));
        assertTrue(employeService.create(dev));

        // 2. Création Projet
        Date debProj = sdf.parse("14/01/2013");
        Date finProj = sdf.parse("31/12/2013");
        Projet projet = new Projet("Gestion de stock", debProj, finProj, chef);
        assertTrue(projetService.create(projet));

        // 3. Vérifier projets gérés par l'employé chef
        List<Projet> geres = employeService.findProjetsGeres(chef);
        assertEquals(1, geres.size());
        assertEquals("Gestion de stock", geres.get(0).getNom());

        // 4. Création Tâches
        Tache t1 = new Tache("Analyse", sdf.parse("01/02/2013"), sdf.parse("28/02/2013"), 1500.0, projet);
        Tache t2 = new Tache("Conception", sdf.parse("01/03/2013"), sdf.parse("31/03/2013"), 2000.0, projet);
        Tache t3 = new Tache("Documentation", sdf.parse("01/05/2013"), sdf.parse("15/05/2013"), 800.0, projet);
        assertTrue(tacheService.create(t1));
        assertTrue(tacheService.create(t2));
        assertTrue(tacheService.create(t3));

        // 5. Vérifier tâches planifiées
        List<Tache> planifiees = projetService.findTachesPlanifiees(projet);
        assertEquals(3, planifiees.size());

        // 6. Affectation des tâches réalisées (dates réelles)
        EmployeTache et1 = new EmployeTache(dev, t1, sdf.parse("10/02/2013"), sdf.parse("20/02/2013"));
        EmployeTache et2 = new EmployeTache(dev, t2, sdf.parse("10/03/2013"), sdf.parse("15/03/2013"));
        assertTrue(employeTacheService.create(et1));
        assertTrue(employeTacheService.create(et2));

        // 7. Vérifier tâches réalisées par l'employé
        List<Tache> tachesDev = employeService.findTachesRealisees(dev);
        assertEquals(2, tachesDev.size());

        // 8. Vérifier la requête nommée (prix > 1000 DH)
        List<Tache> prixSup1000 = tacheService.findPrixSuperieurA1000();
        assertEquals(2, prixSup1000.size()); // t1 (1500) et t2 (2000), mais pas t3 (800)
        for (Tache t : prixSup1000) {
            assertTrue("Le prix doit être supérieur à 1000", t.getPrix() > 1000.0);
        }

        // 9. Vérifier les tâches réalisées entre deux dates (01/02/2013 au 28/02/2013)
        List<Tache> realiseesFevrier = tacheService.findTachesRealiseesEntreDates(
                sdf.parse("01/02/2013"), sdf.parse("28/02/2013"));
        assertEquals(1, realiseesFevrier.size());
        assertEquals("Analyse", realiseesFevrier.get(0).getNom());
    }
}
