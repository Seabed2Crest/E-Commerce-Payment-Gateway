package com.ecommerce.app.schema;

import com.ecommerce.app.entity.publicschema.TenantSubscriptionEntity;
import com.ecommerce.app.repository.publicschema.TenantSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantMigrationRunner implements CommandLineRunner {

    private final TenantSchemaService tenantSchemaService;
    private final TenantSubscriptionRepository tenantSubscriptionRepository;

    @Override
    public void run(String... args) {
        List<TenantSubscriptionEntity> organizations = tenantSubscriptionRepository.findByIsSubscriptionActiveTrue();
        log.info("Total customers found: {}", organizations.size());

        List<String> allTenantSchemas = organizations.stream()
                .flatMap(o -> Stream.of(o.getSchemaName()))
                .distinct()
                .toList();

        log.info("Tenant schemas to migrate: {}", allTenantSchemas);
        tenantSchemaService.runMigrationsForExistingTenants(allTenantSchemas);
    }
}
