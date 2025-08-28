package com.stockflow.core.organization.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganizationPutDTO {

    @NotBlank
    private String tin;

    @NotBlank
    private String name;

    private String address;

    private String phone;

    @NotBlank
    @Email
    private String email;

    private Boolean marketplaceEnabled;
}