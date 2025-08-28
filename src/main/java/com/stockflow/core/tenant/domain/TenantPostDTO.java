package com.stockflow.core.tenant.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TenantPostDTO {

    @NotBlank
    private String displayName;

}