package com.stockflow.core.business.domain;


import com.stockflow.core.organization.domain.OrganizationMother;

public class BusinessMother {

    private static final String NAME = "ACME Corporation";
    private static final String ADDRESS = "1234 Coyote Road, Albuquerque, New Mexico";
    private static final String PHONE = "+1505555ACME";
    private static final String EMAIL = "info@acme-corporation.com";
    private static final Boolean MARKETPLACE_ENABLED = Boolean.FALSE;

    public static final String PHONE_NORMALIZED = "+15055552263";

    private static Business baseBusiness() {
        return Business.builder()
                .organization(OrganizationMother.activeOrganization())
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .marketplaceEnabled(MARKETPLACE_ENABLED)
                .build();
    }

    public static Business activeBusiness() {
        Business business = baseBusiness();
        business.activate();
        return business;
    }

    public static Business inactiveBusiness() {
        Business business = baseBusiness();
        business.deactivate();
        return business;
    }

    public static BusinessPostDTO businessPostDTO() {
        return BusinessPostDTO.builder()
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .build();
    }

    public static BusinessPostDTO businessPostDTOWithoutPhone() {
        return BusinessPostDTO.builder()
                .name(NAME)
                .address(ADDRESS)
                .email(EMAIL)
                .build();
    }

    public static BusinessPostDTO businessPostDTOWithoutEmail() {
        return BusinessPostDTO.builder()
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .build();
    }

    public static BusinessPutDTO businessPutDTO() {
        return BusinessPutDTO.builder()
                .name("Updated Corporation")
                .address("Updated Address")
                .phone("+593987654321")
                .email("updated@acme-corporation.com")
                .marketplaceEnabled(Boolean.TRUE)
                .build();
    }

}