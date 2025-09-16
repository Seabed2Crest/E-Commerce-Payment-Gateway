package com.ecommerce.app.filter;

import com.payment.app.schema.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String tenantId = TenantContextHolder.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            log.info("No tenantId found. Using default schema 'public'");
            TenantContextHolder.setTenantId("public");
        } else {
            tenantId = tenantId.toLowerCase().replaceAll("\\s+", "_");
            YearMonth yearMonth = YearMonth.now();
            String completeSchemaName = String.format("%s_%s" , tenantId, yearMonth.format(DateTimeFormatter.ofPattern("yyyy_MM")));
            TenantContextHolder.setTenantId(completeSchemaName);
        }
        filterChain.doFilter(request, response);

    }
}
