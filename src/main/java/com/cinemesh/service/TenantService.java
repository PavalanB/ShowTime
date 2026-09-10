package com.cinemesh.service;

import com.cinemesh.model.Tenant;
import com.cinemesh.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant createTenant(String tenantId, String name, String city, String address, String contactEmail) {
        if (tenantRepository.existsByTenantId(tenantId)) {
            throw new IllegalArgumentException("Tenant with ID " + tenantId + " already exists.");
        }
        Tenant tenant = new Tenant(tenantId, name, city, address, contactEmail);
        return tenantRepository.save(tenant);
    }

    public Optional<Tenant> getTenant(String tenantId) {
        return tenantRepository.findByTenantId(tenantId);
    }

    public List<Tenant> listAllTenants() {
        return tenantRepository.findAll();
    }
}
