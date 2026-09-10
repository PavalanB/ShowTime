package com.cinemesh.soap.endpoint;

import com.cinemesh.model.Tenant;
import com.cinemesh.service.TenantService;
import com.cinemesh.soap.dto.TenantSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class TenantEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/tenant";
    private final TenantService tenantService;

    public TenantEndpoint(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateTenantRequest")
    @ResponsePayload
    public CreateTenantResponse createTenant(@RequestPayload CreateTenantRequest request) {
        CreateTenantResponse response = new CreateTenantResponse();
        try {
            Tenant tenant = tenantService.createTenant(
                    request.getTenantId(), request.getName(), request.getCity(),
                    request.getAddress(), request.getContactEmail()
            );
            response.setStatus("SUCCESS");
            response.setTenant(mapTenant(tenant));
            response.setMessage("Tenant onboarded successfully");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetTenantRequest")
    @ResponsePayload
    public GetTenantResponse getTenant(@RequestPayload GetTenantRequest request) {
        GetTenantResponse response = new GetTenantResponse();
        Optional<Tenant> tenantOpt = tenantService.getTenant(request.getTenantId());
        if (tenantOpt.isPresent()) {
            response.setStatus("SUCCESS");
            response.setTenant(mapTenant(tenantOpt.get()));
        } else {
            response.setStatus("NOT_FOUND");
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ListTenantsRequest")
    @ResponsePayload
    public ListTenantsResponse listTenants(@RequestPayload ListTenantsRequest request) {
        ListTenantsResponse response = new ListTenantsResponse();
        List<TenantDto> dtos = tenantService.listAllTenants().stream()
                .map(this::mapTenant)
                .collect(Collectors.toList());
        response.setTenants(dtos);
        return response;
    }

    private TenantDto mapTenant(Tenant t) {
        TenantDto dto = new TenantDto();
        dto.setTenantId(t.getTenantId());
        dto.setName(t.getName());
        dto.setCity(t.getCity());
        dto.setAddress(t.getAddress());
        dto.setContactEmail(t.getContactEmail());
        dto.setActive(t.isActive());
        return dto;
    }
}
