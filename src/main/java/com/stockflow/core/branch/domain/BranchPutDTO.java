package com.stockflow.core.branch.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BranchPutDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String email;

    private String phone;

    private String address;

    private Boolean isMain;

}