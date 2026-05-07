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
    titre VARCHAR(100) NOT NULL,
    description TEXT,
    type_evenement VARCHAR(50),
    date_evenement DATE,
    heure_evenement TIME,
    lieu VARCHAR(100),
    statut VARCHAR(20)
);

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

-- Insertion d'un utilisateur de test (admin)
INSERT INTO user (nom, prenom, email, password, role) 
VALUES ('Admin', 'Super', 'admin@smartounsi.com', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE id=id;
