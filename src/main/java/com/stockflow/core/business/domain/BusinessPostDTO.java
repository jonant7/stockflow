package com.stockflow.core.business.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BusinessPostDTO {

    @NotBlank
    private String name;

    private String phone;

    @Email
    private String email;

    @NotBlank
    private String address;

}