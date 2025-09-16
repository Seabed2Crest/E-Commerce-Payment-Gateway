package com.ecommerce.app.schema;

import com.ecommerce.app.exception.type.ResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantSchemaService {

    private final DataSource dataSource;

    public void createSchema(String schemaName) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            YearMonth yearMonth = YearMonth.now();
            String completeSchemaName = String.format("%s_%s" , schemaName, yearMonth.format(DateTimeFormatter.ofPattern("yyyy_MM")));
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + completeSchemaName);
            log.info("Schema created successfully: {}", completeSchemaName);
            runFlywayMigrations(completeSchemaName);
        } catch (SQLException e) {
            log.error("Failed to create schema for tenant: {}", schemaName, e);
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(), "Schema creation failed");
        }
    }

    private void runFlywayMigrations(String schemaName) {
        try {
            Flyway.configure()
                  .dataSource(dataSource)
                  .schemas(schemaName)
                  .locations("classpath:database/tenant-schema")
                    .baselineOnMigrate(true)
                  .load()
                  .migrate();
            log.info("Flyway migrations completed for schema: {}", schemaName);
        } catch (Exception e) {
            log.error("Flyway migration failed for schema: {}", schemaName, e);
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(), "Flyway migration failed");
        }
    }

    public void runMigrationsForExistingTenants(List<String> tenantSchemas) {
        for (String tenant : tenantSchemas) {
            log.info("Running Flyway migration for existing tenant schema: {}", tenant);
            runFlywayMigrations(tenant);
        }
    }
}
