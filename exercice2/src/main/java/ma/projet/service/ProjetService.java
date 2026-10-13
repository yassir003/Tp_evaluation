package ma.projet.service;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
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

public class ProjetService implements IDao<Projet> {

    @Override
    public boolean create(Projet o) {
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
    public boolean update(Projet o) {
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
    public boolean delete(Projet o) {
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
    public Projet findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Projet.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Projet> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Projet", Projet.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 1. Afficher la liste des tâches planifiées pour un projet.
     */
    public List<Tache> findTachesPlanifiees(Projet projet) {
        if (projet == null) return Collections.emptyList();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tache> query = session.createQuery(
                    "FROM Tache t WHERE t.projet.id = :pId ORDER BY t.id", Tache.class);
            query.setParameter("pId", projet.getId());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherTachesPlanifiees(Projet projet) {
        if (projet == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Tâches planifiées pour le projet : " + projet.getNom());
        List<Tache> list = findTachesPlanifiees(projet);
        if (list.isEmpty()) {
            System.out.println(" Aucune tâche planifiée.");
        } else {
            for (Tache t : list) {
                System.out.println(" - " + t.getId() + " " + t.getNom() + " (Début: " +
                        formatDate(sdf, t.getDateDebut()) + ", Fin: " + formatDate(sdf, t.getDateFin()) + ")");
            }
        }
    }

    /**
     * 2. Afficher la liste des tâches réalisées avec les dates réelles.
     * Exemple d'affichage attendu :
     * Projet : 4      Nom : Gestion de stock     Date début : 14 Janvier 2013
     * Liste des tâches:
     * Num Nom            Date Début Réelle   Date Fin Réelle
     * 12  Analyse        10/02/2013          20/02/2013
     * 13  Conception     10/03/2013          15/03/2013
     * 14  Développement  10/04/2013          25/04/2013
     */
    public void afficherTachesRealisees(Projet projet) {
        if (projet == null) return;

        SimpleDateFormat sdfLong = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
        String dateDebutStr = "N/A";
        if (projet.getDateDebut() != null) {
            String formatted = sdfLong.format(projet.getDateDebut());
            String[] parts = formatted.split(" ");
            if (parts.length == 3 && !parts[1].isEmpty()) {
                parts[1] = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1);
                dateDebutStr = String.join(" ", parts);
            } else {
                dateDebutStr = formatted;
            }
        }

        System.out.printf("Projet : %-6d Nom : %-20s Date début : %s%n",
                projet.getId(), projet.getNom(), dateDebutStr);
        System.out.println("Liste des tâches:");
        System.out.println("Num Nom            Date Début Réelle   Date Fin Réelle");

        SimpleDateFormat sdfShort = new SimpleDateFormat("dd/MM/yyyy");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<EmployeTache> query = session.createQuery(
                    "FROM EmployeTache et WHERE et.tache.projet.id = :pId ORDER BY et.tache.id", EmployeTache.class);
            query.setParameter("pId", projet.getId());
            List<EmployeTache> list = query.list();

            for (EmployeTache et : list) {
                int num = et.getTache().getId();
                String nom = et.getTache().getNom();
                String dateDebReelle = et.getDateDebutReelle() != null ? sdfShort.format(et.getDateDebutReelle()) : "N/A";
                String dateFinReelle = et.getDateFinReelle() != null ? sdfShort.format(et.getDateFinReelle()) : "N/A";

                System.out.printf("%-4d%-15s%-20s%s%n", num, nom, dateDebReelle, dateFinReelle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void afficherTachesRealisees(int projetId) {
        Projet p = findById(projetId);
        if (p != null) {
            afficherTachesRealisees(p);
        } else {
            System.out.println("Projet introuvable pour l'identifiant : " + projetId);
        }
    }

    private static String formatDate(SimpleDateFormat sdf, Date date) {
        return date != null ? sdf.format(date) : "N/A";
    }
}
