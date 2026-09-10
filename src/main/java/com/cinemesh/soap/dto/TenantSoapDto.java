package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class TenantSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/tenant";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TenantDto {
        private String tenantId;
        private String name;
        private String city;
        private String address;
        private String contactEmail;
        private boolean active;

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    @XmlRootElement(name = "CreateTenantRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreateTenantRequest {
        private String tenantId;
        private String name;
        private String city;
        private String address;
        private String contactEmail;

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    }

    @XmlRootElement(name = "CreateTenantResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreateTenantResponse {
        private String status;
        private TenantDto tenant;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public TenantDto getTenant() { return tenant; }
        public void setTenant(TenantDto tenant) { this.tenant = tenant; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "GetTenantRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetTenantRequest {
        private String tenantId;

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    }

    @XmlRootElement(name = "GetTenantResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetTenantResponse {
        private String status;
        private TenantDto tenant;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public TenantDto getTenant() { return tenant; }
        public void setTenant(TenantDto tenant) { this.tenant = tenant; }
    }

    @XmlRootElement(name = "ListTenantsRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ListTenantsRequest {}

    @XmlRootElement(name = "ListTenantsResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ListTenantsResponse {
        @XmlElement(name = "tenants")
        private List<TenantDto> tenants;

        public List<TenantDto> getTenants() { return tenants; }
        public void setTenants(List<TenantDto> tenants) { this.tenants = tenants; }
    }
}
