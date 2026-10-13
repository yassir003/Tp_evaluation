package ma.projet.service;

import ma.projet.classes.Employe;
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

public class EmployeService implements IDao<Employe> {

    @Override
    public boolean create(Employe o) {
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
    public boolean update(Employe o) {
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
    public boolean delete(Employe o) {
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
    public Employe findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Employe.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Employe> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Employe", Employe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Afficher la liste des tâches réalisées par un employé.
     */
    public List<Tache> findTachesRealisees(Employe employe) {
        if (employe == null) return Collections.emptyList();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tache> query = session.createQuery(
                    "SELECT DISTINCT et.tache FROM EmployeTache et WHERE et.employe.id = :empId", Tache.class);
            query.setParameter("empId", employe.getId());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherTachesRealisees(Employe employe) {
        if (employe == null) return;
        System.out.println("Tâches réalisées par l'employé : " + employe.getPrenom() + " " + employe.getNom());
        List<Tache> list = findTachesRealisees(employe);
        if (list.isEmpty()) {
            System.out.println(" Aucune tâche réalisée.");
        } else {
            for (Tache t : list) {
                System.out.println(" - " + t.getNom() + " (Prix: " + t.getPrix() + " DH)");
            }
        }
    }

    /**
     * Afficher la liste des projets gérés par un employé.
     */
    public List<Projet> findProjetsGeres(Employe employe) {
        if (employe == null) return Collections.emptyList();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Projet> query = session.createQuery(
                    "FROM Projet p WHERE p.chefDeProjet.id = :empId", Projet.class);
            query.setParameter("empId", employe.getId());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherProjetsGeres(Employe employe) {
        if (employe == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Projets gérés par l'employé : " + employe.getPrenom() + " " + employe.getNom());
        List<Projet> list = findProjetsGeres(employe);
        if (list.isEmpty()) {
            System.out.println(" Aucun projet géré.");
        } else {
            for (Projet p : list) {
                System.out.println(" - " + p.getNom() + " (Début: " + formatDate(sdf, p.getDateDebut()) + ", Fin: " + formatDate(sdf, p.getDateFin()) + ")");
            }
        }
    }

    private static String formatDate(SimpleDateFormat sdf, Date date) {
        return date != null ? sdf.format(date) : "N/A";
    }
}
