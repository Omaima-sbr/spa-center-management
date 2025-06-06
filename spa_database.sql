CREATE DATABASE IF NOT exists spa_database;
USE spa_database;

-- ✅ Clients
CREATE TABLE if not exists clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    telephone VARCHAR(20),
    email VARCHAR(100) UNIQUE
);

-- ✅ Services proposés
CREATE TABLE if not exists services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100),
    description TEXT,
    duree INT, -- durée en minutes
    prix DECIMAL(10,2)
);

-- ✅ Abonnements aux services
CREATE TABLE if not exists  abonnements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    service_id INT,
    date_debut DATE,
    date_fin DATE,
    type_abonnement VARCHAR(50) CHECK (type IN ('Mensuel','Annuel')),
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- ✅ Employés / praticiens
CREATE TABLE if not exists  employes (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    cni VARCHAR(100) ,
	nom VARCHAR(100),
    prenom VARCHAR(100),
    telephone VARCHAR(20),
    specialite VARCHAR(100)
  
);


-- ✅ Table rendez-vous
CREATE TABLE if not exists  rendez_vous (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    service_id INT,
    employe_id INT,
    date_heure DATETIME,
    statut ENUM('planifié', 'effectué', 'annulé') DEFAULT 'planifié',
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (employe_id) REFERENCES employes(id)
);


-- ✅ Paiements
CREATE TABLE if not exists  paiements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    rendez_vous_id INT,
    montant DECIMAL(10,2),
    mode_paiement VARCHAR(50), -- Ex: espèce, carte, virement
    date_paiement DATETIME,
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (rendez_vous_id) REFERENCES rendez_vous(id)
);

INSERT INTO clients (nom, prenom, telephone, email) VALUES
('Dupont', 'Jean', '0123456789', 'jean.dupont@example.com'),
('Martin', 'Claire', '0987654321', 'claire.martin@example.com'),
('Bernard', 'Luc', '0147258369', 'luc.bernard@example.com'),
('Lemoine', 'Sophie', '0162549874', 'sophie.lemoine@example.com'),
('Durand', 'Pierre', '0183764952', 'pierre.durand@example.com');



SELECT * FROM clients;

SELECT * FROM services;



