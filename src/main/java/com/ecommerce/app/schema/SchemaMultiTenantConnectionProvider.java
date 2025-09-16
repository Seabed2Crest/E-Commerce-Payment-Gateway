package com.ecommerce.app.schema;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Slf4j
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final transient DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SuppressWarnings("squid:S2095")
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            if (tenantIdentifier != null) {
                connection.createStatement().execute("SET search_path TO " + tenantIdentifier);
                log.info("Switched to schema: {}", tenantIdentifier);
            }
        } catch (SQLException e) {
            log.error("Failed to switch schema to: {}", tenantIdentifier, e);
            connection.close();
            throw e;
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) {
        try (connection) {
            connection.createStatement().execute("SET search_path TO public");
        } catch (SQLException e) {
            log.warn("Failed to reset schema", e);
        }
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        log.info("Get Any Connection Executed.... Connection: {}", conn.hashCode());
        return conn;
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        log.info("Release Connection Executed....");
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(@NonNull Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(@NonNull Class<T> unwrapType) {
        throw new UnsupportedOperationException("Unwrap is not supported");
    }
}
