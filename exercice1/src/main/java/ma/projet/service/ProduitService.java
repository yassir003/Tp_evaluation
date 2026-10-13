package ma.projet.service;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProduitService implements IDao<Produit> {

    @Override
    public boolean create(Produit o) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Produit o) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Produit o) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Produit findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Produit.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Produit> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Produit", Produit.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 1. Méthode pour afficher la liste des produits par catégorie
     */
    public List<Produit> findByCategorie(Categorie categorie) {
        if (categorie == null) return Collections.emptyList();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Produit> query = session.createQuery(
                    "FROM Produit p WHERE p.categorie.id = :catId", Produit.class);
            query.setParameter("catId", categorie.getId());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherProduitsParCategorie(Categorie categorie) {
        if (categorie == null) {
            System.out.println("Catégorie nulle.");
            return;
        }
        System.out.println("Catégorie : " + categorie.getLibelle());
        System.out.println("Liste des produits :");
        List<Produit> produits = findByCategorie(categorie);
        for (Produit p : produits) {
            System.out.println("- " + p.getReference() + " : " + formatPrix(p.getPrix()) + " DH");
        }
    }

    /**
     * 2. Méthode pour afficher les produits commandés entre deux dates
     */
    public List<Produit> findProduitsCommandesEntreDates(Date dateDebut, Date dateFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Produit> query = session.createQuery(
                    "SELECT DISTINCT l.produit FROM LigneCommandeProduit l " +
                    "WHERE l.commande.date BETWEEN :dateDebut AND :dateFin", Produit.class);
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherProduitsCommandesEntreDates(Date dateDebut, Date dateFin) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Produits commandés entre " + sdf.format(dateDebut) + " et " + sdf.format(dateFin) + " :");
        List<Produit> produits = findProduitsCommandesEntreDates(dateDebut, dateFin);
        for (Produit p : produits) {
            System.out.println("- " + p.getReference() + " (" + formatPrix(p.getPrix()) + " DH)");
        }
    }

    /**
     * 3. Méthode pour afficher les produits commandés dans une commande donnée
     */
    public void afficherProduitsParCommande(Commande commande) {
        if (commande == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
        String dateFormatted = sdf.format(commande.getDate());
        String[] parts = dateFormatted.split(" ");
        if (parts.length == 3 && !parts[1].isEmpty()) {
            parts[1] = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1);
            dateFormatted = String.join(" ", parts);
        }

        System.out.println("Commande : " + commande.getId() + "     Date : " + dateFormatted);
        System.out.println("Liste des produits :");
        System.out.printf("%-11s %-7s %s%n", "Référence", "Prix", "Quantité");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LigneCommandeProduit> query = session.createQuery(
                    "FROM LigneCommandeProduit l WHERE l.commande.id = :cmdId", LigneCommandeProduit.class);
            query.setParameter("cmdId", commande.getId());
            List<LigneCommandeProduit> lignes = query.list();
            for (LigneCommandeProduit l : lignes) {
                String ref = l.getProduit().getReference();
                String prixStr = formatPrix(l.getProduit().getPrix()) + " DH";
                int quantite = l.getQuantite();
                System.out.printf("%-11s %-7s %d%n", ref, prixStr, quantite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void afficherProduitsParCommande(int commandeId) {
        CommandeService cs = new CommandeService();
        Commande cmd = cs.findById(commandeId);
        if (cmd != null) {
            afficherProduitsParCommande(cmd);
        } else {
            System.out.println("Commande non trouvée pour le numéro : " + commandeId);
        }
    }

    /**
     * 4. Méthode pour afficher la liste des produits dont le prix est supérieur à 100 DH,
     * en utilisant une requête nommée.
     */
    public List<Produit> findPrixSuperieurA100() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Produit> query = session.createNamedQuery("findProduitPrixSuperieurA100", Produit.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherProduitsPrixSuperieurA100() {
        System.out.println("Liste des produits dont le prix est supérieur à 100 DH (Requête nommée) :");
        List<Produit> list = findPrixSuperieurA100();
        for (Produit p : list) {
            System.out.println("- " + p.getReference() + " : " + formatPrix(p.getPrix()) + " DH");
        }
    }

    private static String formatPrix(float prix) {
        if (prix == (long) prix) {
            return String.valueOf((long) prix);
        }
        return String.format(Locale.US, "%.2f", prix);
    }
}
