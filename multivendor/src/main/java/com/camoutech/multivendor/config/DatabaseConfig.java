package com.camoutech.multivendor.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Configuration personnalisée pour la base de données
 * Optimisée pour les connexions distantes avec gestion des timeouts
 */
@Configuration
@ConditionalOnProperty(name = "spring.datasource.custom-config", havingValue = "true", matchIfMissing = false)
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // Configuration de base
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Configuration du pool pour connexions distantes
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000); // 5 minutes
        config.setMaxLifetime(600000); // 10 minutes (réduit pour éviter les timeouts)
        config.setConnectionTimeout(30000); // 30 secondes
        config.setValidationTimeout(10000); // 10 secondes
        
        // Configuration pour la stabilité des connexions distantes
        config.setLeakDetectionThreshold(60000);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("MultivendorHikariCP");
        
        // Paramètres pour maintenir les connexions actives
        config.setKeepaliveTime(30000); // 30 secondes
        config.setInitializationFailTimeout(1);
        
        // Configuration pour éviter les problèmes de réseau
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "false");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        // Paramètres de timeout réseau
        config.addDataSourceProperty("connectTimeout", "30000");
        config.addDataSourceProperty("socketTimeout", "60000");
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("maxReconnects", "10");
        config.addDataSourceProperty("failOverReadOnly", "false");
        
        return new HikariDataSource(config);
    }
}
