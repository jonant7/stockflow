package com.stockflow.core.tenant.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantPutDTO {

    @NotBlank
    private String displayName;

    private String slug;

}