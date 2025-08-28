package com.stockflow.core.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum IdentificationType {

    RUC("04"),
    ID_CARD("05"),
    PASSPORT("06"),
    FINAL_CONSUMER("07"),
    FOREIGN_IDENTIFICATION("08");

    private final String code;

}