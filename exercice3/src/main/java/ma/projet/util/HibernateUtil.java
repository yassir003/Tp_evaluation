package ma.projet.util;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.beans.Personne;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            Properties properties = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    properties.load(input);
                } else {
                    System.err.println("Fichier application.properties introuvable dans le classpath.");
                }
            }

            // Vérifier la joignabilité de la base de données cible (ex: MySQL)
            if (!isDatabaseReachable(properties)) {
                System.out.println("[INFO] Serveur MySQL non disponible. Utilisation de la base de données H2 en mémoire pour l'exercice 3.");
                properties.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
                properties.setProperty("hibernate.connection.url", "jdbc:h2:mem:etat_civil;DB_CLOSE_DELAY=-1");
                properties.setProperty("hibernate.connection.username", "sa");
                properties.setProperty("hibernate.connection.password", "");
                properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
                properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            }

            configuration.setProperties(properties);

            // Enregistrement des entités de l'exercice 3
            configuration.addAnnotatedClass(Personne.class);
            configuration.addAnnotatedClass(Homme.class);
            configuration.addAnnotatedClass(Femme.class);
            configuration.addAnnotatedClass(Mariage.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Échec de la création de la SessionFactory : " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static boolean isDatabaseReachable(Properties props) {
        String driver = props.getProperty("hibernate.connection.driver_class");
        String url = props.getProperty("hibernate.connection.url");
        String user = props.getProperty("hibernate.connection.username", "");
        String pass = props.getProperty("hibernate.connection.password", "");

        if (driver == null || url == null) {
            return false;
        }

        try {
            Class.forName(driver);
            DriverManager.setLoginTimeout(2);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        try {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Throwable ignored) {
        }
    }
}
