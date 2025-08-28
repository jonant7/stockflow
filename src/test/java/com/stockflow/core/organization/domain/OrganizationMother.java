package com.stockflow.core.organization.domain;


public class OrganizationMother {

    private static final String TIN = "1700000001001";
    private static final String NAME = "ACME Corporation";
    private static final String ADDRESS = "1234 Coyote Road, Albuquerque, New Mexico";
    private static final String PHONE = "+1505555ACME";
    private static final String EMAIL = "info@acme-corporation.com";
    private static final Boolean MARKETPLACE_ENABLED = Boolean.FALSE;

    public static final String PHONE_NORMALIZED = "+15055552263";

    private static Organization baseOrganization() {
        return Organization.builder()
                .tin(TIN)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .marketplaceEnabled(MARKETPLACE_ENABLED)
                .build();
    }

    public static Organization activeOrganization() {
        Organization organization = baseOrganization();
        organization.activate();
        return organization;
    }

    public static Organization inactiveOrganization() {
        Organization organization = baseOrganization();
        organization.deactivate();
        return organization;
    }

    public static OrganizationPostDTO organizationPostDTO() {
        return OrganizationPostDTO.builder()
                .tin(TIN)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .build();
    }

    public static OrganizationPutDTO organizationPutDTO() {
        return OrganizationPutDTO.builder()
                .tin(TIN)
                .name("Updated Corporation")
                .address("Updated Address")
                .phone("+593987654321")
                .email("updated@acme-corporation.com")
                .marketplaceEnabled(Boolean.TRUE)
                .build();
    }

}