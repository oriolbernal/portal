package com.obernal.portal_monitoratge.app.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DbConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(DbConnectionPool.class);

    private final Properties properties;
    private final Map<String, Connection> connexions;
    private final Map<String, LocalDateTime> lastUsed;
    public DbConnectionPool(Properties properties) {
        logger.info("Initializing DBPool instance...");
        this.connexions = new ConcurrentHashMap<>();
        this.lastUsed = new ConcurrentHashMap<>();
        this.properties = properties;
    }

    public String[] getDatasources() {
        return properties.getProperty("datasources", "").split(",");
    }

    public synchronized Connection getConnection(String datasource) throws SQLException, ClassNotFoundException {
        if (!connexions.containsKey(datasource) || connexions.get(datasource).isClosed()) {
            logger.info("Starting new connection: {}", datasource);
            connexions.put(datasource, connect(datasource));
        }
        lastUsed.put(datasource, LocalDateTime.now());
        return connexions.get(datasource);
    }

    private Connection connect(String datasource) throws SQLException, ClassNotFoundException {
        String driver = properties.getProperty(datasource + ".driver");
        String url = properties.getProperty(datasource + ".url");
        String username = properties.getProperty(datasource + ".user");
        String password = properties.getProperty(datasource + ".password");
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public synchronized void disconnectAfterXMinutesWithoutUse(int minutes) {
        lastUsed.forEach((datasource, localDateTime) -> {
            if (localDateTime.isBefore(LocalDateTime.now().minusMinutes(minutes))){
                logger.info("Desconnectant-se de {} per no utilitzar-se als últims {} minuts.", datasource, minutes);
                disconnect(datasource);
            }
        });
    }

    private void disconnect(String datasource) {
        try {
            connexions.remove(datasource).close();
            lastUsed.remove(datasource);
        } catch (SQLException e) {
            logger.error("Error al desconnectar-se de: {}", datasource, e);
        }
    }

}
