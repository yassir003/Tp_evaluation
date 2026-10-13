package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HommeService implements IDao<Homme> {

    @Override
    public boolean create(Homme o) {
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
    public boolean update(Homme o) {
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
    public boolean delete(Homme o) {
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
    public Homme findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Homme.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Homme> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Homme", Homme.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 1. Afficher les épouses d'un homme entre deux dates.
     */
    public List<Femme> findEpousesEntreDates(Homme homme, Date dateDebut, Date dateFin) {
        if (homme == null) return Collections.emptyList();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Femme> query = session.createQuery(
                    "SELECT DISTINCT m.femme FROM Mariage m " +
                    "WHERE m.homme.id = :hId AND m.dateDebut BETWEEN :dDebut AND :dFin", Femme.class);
            query.setParameter("hId", homme.getId());
            query.setParameter("dDebut", dateDebut);
            query.setParameter("dFin", dateFin);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void afficherEpousesEntreDates(Homme homme, Date dateDebut, Date dateFin) {
        if (homme == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Épouses de " + homme.getNom() + " " + homme.getPrenom() +
                " entre " + sdf.format(dateDebut) + " et " + sdf.format(dateFin) + " :");
        List<Femme> epouses = findEpousesEntreDates(homme, dateDebut, dateFin);
        if (epouses.isEmpty()) {
            System.out.println(" Aucune épouse trouvée dans cet intervalle.");
        } else {
            for (Femme f : epouses) {
                System.out.println(" - " + f.getPrenom() + " " + f.getNom() + " (Tél: " + f.getTelephone() + ")");
            }
        }
    }

    /**
     * 2. Méthode utilisant l'API Criteria pour afficher le nombre d'hommes mariés à quatre femmes entre deux dates.
     */
    public int countHommesMariesQuatreFemmesEntreDates(Date dateDebut, Date dateFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Homme> cq = cb.createQuery(Homme.class);
            Root<Mariage> root = cq.from(Mariage.class);

            // Mariage actif ou conclu dans l'intervalle
            Predicate datePredicate = cb.and(
                    cb.lessThanOrEqualTo(root.get("dateDebut"), dateFin),
                    cb.or(
                            cb.isNull(root.get("dateFin")),
                            cb.greaterThanOrEqualTo(root.get("dateFin"), dateDebut)
                    )
            );

            cq.select(root.get("homme"));
            cq.where(datePredicate);
            cq.groupBy(root.get("homme"));
            cq.having(cb.greaterThanOrEqualTo(cb.countDistinct(root.get("femme")), 4L));

            List<Homme> hommes = session.createQuery(cq).getResultList();
            return hommes.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void afficherNombreHommesMariesQuatreFemmesEntreDates(Date dateDebut, Date dateFin) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int count = countHommesMariesQuatreFemmesEntreDates(dateDebut, dateFin);
        System.out.println("Nombre d'hommes mariés à 4 femmes entre " + sdf.format(dateDebut) +
                " et " + sdf.format(dateFin) + " (API Criteria) : " + count);
    }

    /**
     * 3. Afficher les mariages d'un homme donné avec les détails.
     * Exemple d'affichage attendu :
     * Nom : SAFI SAID
     * Mariages En Cours :
     * 1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4
     * 2. Femme : AMAL ALI      Date Début : 03/09/1995    Nbr Enfants : 2
     * 3. Femme : WAFA ALAOUI   Date Début : 04/11/2000    Nbr Enfants : 3
     *
     * Mariages échoués :
     * 1. Femme : KARIMA ALAMI  Date Début : 03/09/1989
     * Date Fin : 03/09/1990    Nbr Enfants : 0
     */
    public void afficherMariagesParHomme(Homme homme) {
        if (homme == null) return;
        System.out.println("Nom : " + (homme.getNom() + " " + homme.getPrenom()).toUpperCase());

        List<Mariage> mariages;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Mariage> query = session.createQuery(
                    "FROM Mariage m WHERE m.homme.id = :hId ORDER BY m.dateDebut ASC", Mariage.class);
            query.setParameter("hId", homme.getId());
            mariages = query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        List<Mariage> enCours = new ArrayList<>();
        List<Mariage> echoues = new ArrayList<>();
        for (Mariage m : mariages) {
            if (m.getDateFin() == null) {
                enCours.add(m);
            } else {
                echoues.add(m);
            }
        }

        System.out.println("Mariages En Cours :");
        int idx = 1;
        for (Mariage m : enCours) {
            String femmeNom = (m.getFemme().getPrenom() + " " + m.getFemme().getNom()).toUpperCase();
            String dateDeb = sdf.format(m.getDateDebut());
            System.out.printf("%d. Femme : %-15s Date Début : %-13s Nbr Enfants : %d%n",
                    idx++, femmeNom, dateDeb, m.getNbrEnfant());
        }

        System.out.println("\nMariages échoués :");
        idx = 1;
        for (Mariage m : echoues) {
            String femmeNom = (m.getFemme().getPrenom() + " " + m.getFemme().getNom()).toUpperCase();
            String dateDeb = sdf.format(m.getDateDebut());
            String dateFin = sdf.format(m.getDateFin());
            System.out.printf("%d. Femme : %-15s Date Début : %s    %nDate Fin : %-13s Nbr Enfants : %d%n",
                    idx++, femmeNom, dateDeb, dateFin, m.getNbrEnfant());
        }
    }

    public void afficherMariagesParHomme(int hommeId) {
        Homme h = findById(hommeId);
        if (h != null) {
            afficherMariagesParHomme(h);
        } else {
            System.out.println("Homme introuvable avec l'identifiant : " + hommeId);
        }
    }
}
