package ma.projet.service;

import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.LigneCommandeProduitPK;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class LigneCommandeService implements IDao<LigneCommandeProduit> {

    @Override
    public boolean create(LigneCommandeProduit o) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (o.getPk() == null && o.getProduit() != null && o.getCommande() != null) {
                o.setPk(new LigneCommandeProduitPK(o.getProduit().getId(), o.getCommande().getId()));
            }
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
    public boolean update(LigneCommandeProduit o) {
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
    public boolean delete(LigneCommandeProduit o) {
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
    public LigneCommandeProduit findById(int id) {
        // Pour une clé primaire composite, cette surcharge est fournie par le contrat IDao
        return null;
    }

    public LigneCommandeProduit findById(LigneCommandeProduitPK pk) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(LigneCommandeProduit.class, pk);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LigneCommandeProduit findById(int produitId, int commandeId) {
        return findById(new LigneCommandeProduitPK(produitId, commandeId));
    }

    public List<LigneCommandeProduit> findByCommande(Commande commande) {
        if (commande == null) return Collections.emptyList();
        return findByCommandeId(commande.getId());
    }

    public List<LigneCommandeProduit> findByCommandeId(int commandeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LigneCommandeProduit> query = session.createQuery(
                    "FROM LigneCommandeProduit l WHERE l.commande.id = :cmdId", LigneCommandeProduit.class);
            query.setParameter("cmdId", commandeId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<LigneCommandeProduit> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from LigneCommandeProduit", LigneCommandeProduit.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
