package ma.projet.service;

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

public class TacheService implements IDao<Tache> {

    @Override
    public boolean create(Tache o) {
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
    public boolean update(Tache o) {
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
    public boolean delete(Tache o) {
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
    public Tache findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Tache.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Tache> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Tache", Tache.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 1. Méthode pour afficher les tâches dont le prix est supérieur à 1000 DH (requête nommée).
     */
    public List<Tache> findPrixSuperieurA1000() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tache> query = session.createNamedQuery("findTachePrixSuperieurA1000", Tache.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherTachesPrixSuperieurA1000() {
        System.out.println("Tâches dont le prix est supérieur à 1000 DH (Requête nommée) :");
        List<Tache> list = findPrixSuperieurA1000();
        if (list.isEmpty()) {
            System.out.println(" Aucune tâche trouvée.");
        } else {
            for (Tache t : list) {
                System.out.println(" - " + t.getNom() + " : " + formatPrix(t.getPrix()) + " DH");
            }
        }
    }

    /**
     * 2. Méthode pour afficher les tâches réalisées entre deux dates.
     */
    public List<Tache> findTachesRealiseesEntreDates(Date dateDebut, Date dateFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tache> query = session.createQuery(
                    "SELECT DISTINCT et.tache FROM EmployeTache et " +
                    "WHERE (et.dateDebutReelle BETWEEN :dDebut AND :dFin) " +
                    "   OR (et.dateFinReelle BETWEEN :dDebut AND :dFin) " +
                    "   OR (et.dateDebutReelle <= :dDebut AND et.dateFinReelle >= :dFin)", Tache.class);
            query.setParameter("dDebut", dateDebut);
            query.setParameter("dFin", dateFin);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherTachesRealiseesEntreDates(Date dateDebut, Date dateFin) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Tâches réalisées entre le " + sdf.format(dateDebut) + " et le " + sdf.format(dateFin) + " :");
        List<Tache> list = findTachesRealiseesEntreDates(dateDebut, dateFin);
        if (list.isEmpty()) {
            System.out.println(" Aucune tâche trouvée.");
        } else {
            for (Tache t : list) {
                System.out.println(" - " + t.getId() + " " + t.getNom() + " (" + formatPrix(t.getPrix()) + " DH)");
            }
        }
    }

    private static String formatPrix(double prix) {
        if (prix == (long) prix) {
            return String.valueOf((long) prix);
        }
        return String.format("%.2f", prix);
    }
}
