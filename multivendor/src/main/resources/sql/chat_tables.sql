-- Script SQL pour créer les tables de chat
-- À exécuter dans votre base de données MySQL

-- Table des sessions de chat
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT,
    user_email VARCHAR(255),
    title VARCHAR(255) DEFAULT 'Nouvelle conversation',
    is_active BOOLEAN DEFAULT TRUE,
    message_count INT DEFAULT 0,
    total_tokens BIGINT DEFAULT 0,
    average_response_time DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_user_email (user_email),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at),
    INDEX idx_last_activity (last_activity_at)
);

-- Table des messages de chat
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_session_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_user BOOLEAN NOT NULL,
    response_time DOUBLE,
    tokens BIGINT,
    rating ENUM('POSITIVE', 'NEGATIVE', 'NEUTRAL'),
    message_order INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (chat_session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE,
    INDEX idx_chat_session_id (chat_session_id),
    INDEX idx_is_user (is_user),
    INDEX idx_created_at (created_at),
    INDEX idx_rating (rating),
    INDEX idx_message_order (message_order)
);

-- Insertion de données de test (optionnel)
-- INSERT INTO chat_sessions (session_id, user_id, user_email, title) VALUES 
-- ('chat_test123', 1, 'test@example.com', 'Session de test');

-- INSERT INTO chat_messages (chat_session_id, content, is_user, message_order) VALUES 
-- (1, 'Bonjour, comment puis-je vous aider ?', FALSE, 1),
-- (1, 'Je cherche des produits biologiques', TRUE, 2),
-- (1, 'Voici nos meilleurs produits biologiques disponibles...', FALSE, 3);
