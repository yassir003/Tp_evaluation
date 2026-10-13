package ma.projet;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.MariageService;
import ma.projet.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class EtatCivilTest {

    private static HommeService hommeService;
    private static FemmeService femmeService;
    private static MariageService mariageService;

    @BeforeClass
    public static void setUp() {
        hommeService = new HommeService();
        femmeService = new FemmeService();
        mariageService = new MariageService();
    }

    @AfterClass
    public static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    public void testEtatCivilComplete() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // 1. Création 2 hommes et 4 femmes pour test
        Homme h1 = new Homme("SAFI", "Said", "06001", "Casablanca", sdf.parse("01/01/1965"));
        Homme h2 = new Homme("TAZI", "Karim", "06002", "Rabat", sdf.parse("01/01/1970"));
        assertTrue(hommeService.create(h1));
        assertTrue(hommeService.create(h2));

        Femme f1 = new Femme("RAMI", "Salima", "06101", "Rabat", sdf.parse("15/04/1968"));
        Femme f2 = new Femme("ALI", "Amal", "06102", "Casablanca", sdf.parse("20/06/1972"));
        Femme f3 = new Femme("ALAOUI", "Wafa", "06103", "Fès", sdf.parse("10/11/1975"));
        Femme f4 = new Femme("ALAMI", "Karima", "06104", "Marrakech", sdf.parse("05/01/1960")); // La plus âgée
        assertTrue(femmeService.create(f1));
        assertTrue(femmeService.create(f2));
        assertTrue(femmeService.create(f3));
        assertTrue(femmeService.create(f4));

        // 2. Vérification femme la plus âgée
        Femme plusAgee = femmeService.findFemmePlusAgee();
        assertNotNull(plusAgee);
        assertEquals("ALAMI", plusAgee.getNom());

        // 3. Création des mariages
        // Mariage échoué h1 - f4
        Mariage m1 = new Mariage(h1, f4, sdf.parse("03/09/1989"), sdf.parse("03/09/1990"), 0);
        // Mariages en cours h1 - f1, f2, f3
        Mariage m2 = new Mariage(h1, f1, sdf.parse("03/09/1990"), null, 4);
        Mariage m3 = new Mariage(h1, f2, sdf.parse("03/09/1995"), null, 2);
        Mariage m4 = new Mariage(h1, f3, sdf.parse("04/11/2000"), null, 3);
        // Remariage f4 avec h2
        Mariage m5 = new Mariage(h2, f4, sdf.parse("15/06/1993"), null, 1);

        assertTrue(mariageService.create(m1));
        assertTrue(mariageService.create(m2));
        assertTrue(mariageService.create(m3));
        assertTrue(mariageService.create(m4));
        assertTrue(mariageService.create(m5));

        // 4. Test épouses d'un homme entre deux dates
        List<Femme> epouses = hommeService.findEpousesEntreDates(h1, sdf.parse("01/01/1990"), sdf.parse("31/12/2000"));
        assertEquals(3, epouses.size()); // f1, f2, f3

        // 5. Test requête native nommée : nombre d'enfants d'une femme
        int nbEnfantsF1 = femmeService.countEnfantsEntreDates(f1, sdf.parse("01/01/1985"), sdf.parse("31/12/2000"));
        assertEquals(4, nbEnfantsF1);

        // 6. Test requête nommée : femmes mariées au moins deux fois
        List<Femme> marieesDeuxFois = femmeService.findFemmesMarieesAuMoinsDeuxFois();
        assertEquals(1, marieesDeuxFois.size());
        assertEquals("ALAMI", marieesDeuxFois.get(0).getNom()); // f4 mariée avec h1 puis h2
    }
}
