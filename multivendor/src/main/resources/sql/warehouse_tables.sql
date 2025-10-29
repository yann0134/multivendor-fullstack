-- Script SQL pour créer les tables nécessaires au système d'entrepôt
-- Table pour les livreurs
CREATE TABLE IF NOT EXISTS delivery_persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    vehicle_type VARCHAR(50), -- 'bike', 'car', 'van'
    license_number VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insérer quelques livreurs de test
INSERT INTO delivery_persons (name, phone, email, vehicle_type, license_number) VALUES
('Jean Dupont', '06 12 34 56 78', 'jean.dupont@agrimarket.com', 'car', 'ABC123'),
('Marie Martin', '06 87 65 43 21', 'marie.martin@agrimarket.com', 'bike', 'DEF456'),
('Pierre Durand', '06 98 76 54 32', 'pierre.durand@agrimarket.com', 'van', 'GHI789'),
('Sophie Leroy', '06 11 22 33 44', 'sophie.leroy@agrimarket.com', 'car', 'JKL012');

-- Ajouter une colonne pour les notes de livraison dans la table orders si elle n'existe pas
ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivery_notes TEXT;

-- Mettre à jour les commandes existantes pour avoir un statut par défaut
UPDATE orders SET order_status = 'PENDING' WHERE order_status IS NULL;

-- Créer un index sur le statut des commandes pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(order_status);
CREATE INDEX IF NOT EXISTS idx_orders_delivery_status ON orders(delivery_status);
CREATE INDEX IF NOT EXISTS idx_orders_delivery_person ON orders(delivery_person_id);


