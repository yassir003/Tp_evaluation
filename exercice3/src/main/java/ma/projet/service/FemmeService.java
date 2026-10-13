package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FemmeService implements IDao<Femme> {

    @Override
    public boolean create(Femme o) {
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
    public boolean update(Femme o) {
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
    public boolean delete(Femme o) {
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
    public Femme findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Femme.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Femme> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Femme", Femme.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 1. Méthode pour exécuter une requête native nommée retournant le nombre d'enfants
     * d'une femme entre deux dates.
     */
    public int countEnfantsEntreDates(Femme femme, Date dateDebut, Date dateFin) {
        if (femme == null) return 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery<?> query = session.getNamedNativeQuery("countEnfantsEntreDates");
            query.setParameter("femmeId", femme.getId());
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);
            Object result = query.getSingleResult();
            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void afficherNombreEnfantsEntreDates(Femme femme, Date dateDebut, Date dateFin) {
        if (femme == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int count = countEnfantsEntreDates(femme, dateDebut, dateFin);
        System.out.println("Nombre d'enfants pour " + femme.getPrenom() + " " + femme.getNom() +
                " entre le " + sdf.format(dateDebut) + " et le " + sdf.format(dateFin) + " (Requête native nommée) : " + count);
    }

    /**
     * 2. Méthode pour exécuter une requête nommée retournant les femmes mariées au moins deux fois.
     */
    public List<Femme> findFemmesMarieesAuMoinsDeuxFois() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Femme> query = session.createNamedQuery("findFemmesMarieesAuMoinsDeuxFois", Femme.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherFemmesMarieesAuMoinsDeuxFois() {
        System.out.println("Femmes mariées au moins deux fois (Requête nommée) :");
        List<Femme> femmes = findFemmesMarieesAuMoinsDeuxFois();
        if (femmes.isEmpty()) {
            System.out.println(" Aucune femme trouvée.");
        } else {
            for (Femme f : femmes) {
                System.out.println(" - " + f.getPrenom() + " " + f.getNom() + " (Tél: " + f.getTelephone() + ")");
            }
        }
    }

    /**
     * 3. Afficher la femme la plus âgée.
     */
    public Femme findFemmePlusAgee() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Femme> query = session.createNamedQuery("findFemmePlusAgee", Femme.class);
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void afficherFemmePlusAgee() {
        Femme f = findFemmePlusAgee();
        if (f != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Femme la plus âgée : " + f.getPrenom() + " " + f.getNom() +
                    " (Née le : " + (f.getDateNaissance() != null ? sdf.format(f.getDateNaissance()) : "N/A") + ")");
        } else {
            System.out.println("Aucune femme enregistrée.");
        }
    }
}
