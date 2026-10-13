# TP Évaluation

Ce projet regroupe les **3 exercices** d'évaluation en JEE portant sur la mise en œuvre de la persistance objet-relationnelle avec **JPA / Hibernate**, le découpage en couches (Beans/Entités, DAO générique, Services métier) et l'utilisation de requêtes avancées (HQL, Requêtes nommées, Requêtes natives SQL et API Criteria).

---

## Sommaire

1. [Structure globale du projet](#structure-globale-du-projet)
2. [Instructions d'exécution](#instructions-dexécution)
3. [Exercice 1 — Application de Gestion de Stock](#exercice-1--application-de-gestion-de-stock)
4. [Exercice 2 — Application de Gestion de Projets](#exercice-2--application-de-gestion-de-projets)
5. [Exercice 3 — Application de Gestion de l'État Civil](#exercice-3--application-de-gestion-de-létat-civil)
6. [Technologies et Dépendances](#technologies-et-dépendances)

---

## Structure globale du projet

Le projet est organisé sous la forme d'un projet multi-modules Maven garantissant l'indépendance de chaque exercice dans son propre répertoire :

```
evaluation/
├── pom.xml                                 # POM parent multi-modules
├── App.java                                # Exécuteur global (java App.java [1|2|3|all])
├── screenshots/                            # Captures d'écran d'exécution
│   ├── exercice1_execution.png
│   ├── exercice2_execution.png
│   ├── exercice3_execution.png
├── src/main/java/com/example/
│   ├── App.java                            # Classe principale globale
│   └── Main.java                           # Redirection vers App
│
├── exercice1/                              # EXERCICE 1 : Gestion de Stock
│   ├── pom.xml
│   └── src/main/java/ma/projet/
│       ├── classes/                        # Categorie, Produit, Commande, LigneCommandeProduit
│       ├── dao/                            # IDao<T>
│       ├── service/                        # ProduitService, CategorieService, CommandeService, ...
│       ├── util/                           # HibernateUtil
│       └── test/                           # Test.java, App.java
│
├── exercice2/                              # EXERCICE 2 : Gestion de Projets
│   ├── pom.xml
│   └── src/main/java/ma/projet/
│       ├── classes/                        # Employe, Projet, Tache, EmployeTache
│       ├── dao/                            # IDao<T>
│       ├── service/                        # ProjetService, TacheService, EmployeService, ...
│       ├── util/                           # HibernateUtil
│       └── test/                           # Test.java, App.java
│
└── exercice3/                              # EXERCICE 3 : Gestion de l'État Civil
    ├── pom.xml
    └── src/main/java/ma/projet/
        ├── beans/                          # Personne (Abstraite), Homme, Femme, Mariage
        ├── dao/                            # IDao<T>
        ├── service/                        # HommeService, FemmeService, MariageService
        ├── util/                           # HibernateUtil
        └── test/                           # Test.java, App.java
```

---

## Instructions d'exécution

### 1. Lancer les tests directement en ligne de commande

À la racine du projet (`evaluation/`), exécutez simplement :

```powershell
# Exécuter les 3 exercices à la suite :
java App.java all

# Exécuter un exercice spécifique :
java App.java 1    # Exercice 1 (Stock)
java App.java 2    # Exercice 2 (Projets)
java App.java 3    # Exercice 3 (État Civil)
```

### 2. Lancer via Maven

```powershell
# Compiler et exécuter tous les tests unitaires :
mvn clean test

# Exécuter le programme de test d'un sous-module :
mvn -pl exercice1 exec:java
mvn -pl exercice2 exec:java
mvn -pl exercice3 exec:java
```

### 3. Lancer depuis IntelliJ IDEA / Eclipse / VS Code
* Ouvrez le fichier `App.java` (ou `src/main/java/com/example/Main.java`).
* Cliquez sur **Run (Bouton ▶ vert)** ou faites **Ctrl + Shift + F10**.

---

## Exercice 1 — Application de Gestion de Stock

### Contexte & Objectif
Gestion du catalogue de produits informatiques, de leurs catégories et des commandes clients passées avec le détail des lignes de commande (prix unitaire, quantité).

### Capture d'exécution
![Exécution Exercice 1](screenshots/exercice1_execution.png)

---

## Exercice 2 — Application de Gestion de Projets

### Contexte & Objectif
Suivi du temps passé sur les projets d'un bureau d'études, planification des tâches et calcul des coûts globaux avec dates réelles de réalisation.


### Capture d'exécution
![Exécution Exercice 2](screenshots/exercice2_execution.png)

---

## Exercice 3 — Application de Gestion de l'État Civil

### Contexte & Objectif
Gestion des citoyens d'une province et de leurs relations matrimoniales (mariages en cours, mariages passés/échoués, nombre d'enfants).


### Capture d'exécution
![Exécution Exercice 3](screenshots/exercice3_execution.png)

---

## Technologies et Dépendances

* **Langage** : Java 21 / 17
* **Framework ORM** : Hibernate Core `5.6.15.Final`
* **Spécification de persistance** : JPA 2.2 (`javax.persistence-api`)
* **Bases de données** :
  * **MySQL 8.0** (`mysql-connector-j 8.0.33`) configuré dans `application.properties`.
  * **H2 Database** (`h2 2.2.224`) configuré en fallback mémoire automatique dans `HibernateUtil` garantissant l'exécution immédiate même si le serveur MySQL local est éteint.
* **Build & Gestion des dépendances** : Apache Maven (Multi-modules Reactor).
* **Tests unitaires** : JUnit 4.13.2.
