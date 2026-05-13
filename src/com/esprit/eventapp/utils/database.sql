CREATE DATABASE IF NOT EXISTS plateforme_education;
USE plateforme_education;

-- Table User
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

-- Table Evenement
CREATE TABLE IF NOT EXISTS evenement (
    id_evenement INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    titre VARCHAR(100) NOT NULL,
    description TEXT,
    type_evenement VARCHAR(50),
    start_date_time DATETIME,
    end_date_time DATETIME,
    lieu VARCHAR(100),
    statut VARCHAR(20) DEFAULT 'A_VENIR',
    image VARCHAR(500),
    prix DOUBLE DEFAULT 0,
    places_disponibles INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
);

-- Script de migration pour mettre à jour une base existante (A COPIER DANS PHPMYADMIN)
-- ALTER TABLE evenement CHANGE COLUMN createur_id user_id INT;
-- ALTER TABLE evenement ADD COLUMN IF NOT EXISTS start_date_time DATETIME;
-- ALTER TABLE evenement ADD COLUMN IF NOT EXISTS end_date_time DATETIME;
-- ALTER TABLE evenement DROP COLUMN IF EXISTS date_evenement;
-- ALTER TABLE evenement DROP COLUMN IF EXISTS heure_evenement;

-- Table AvisEvenement
CREATE TABLE IF NOT EXISTS avis_evenement (
    id_avis INT AUTO_INCREMENT PRIMARY KEY,
    id_evenement INT,
    nom_auteur VARCHAR(100),
    commentaire TEXT,
    note INT,
    date_avis TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_evenement) REFERENCES evenement(id_evenement) ON DELETE CASCADE
);

-- Table Participation
CREATE TABLE IF NOT EXISTS participation (
    id_participation INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    id_evenement INT,
    date_participation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (id_evenement) REFERENCES evenement(id_evenement) ON DELETE CASCADE
);

-- Insertion d'un utilisateur de test (admin)
INSERT INTO user (nom, prenom, email, password, role) 
VALUES ('Admin', 'Super', 'admin@smartounsi.com', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE id=id;

-- Sur une base existante, si ces colonnes manquent (MySQL) :
-- ALTER TABLE evenement ADD COLUMN image VARCHAR(500) NULL;
-- ALTER TABLE evenement ADD COLUMN prix DOUBLE DEFAULT 0;
-- ALTER TABLE evenement ADD COLUMN places_disponibles INT DEFAULT 0;
