package com.stockflow.core.shared.domain;


public enum TriStateBoolean {
    TRUE, FALSE, BOTH;

    public boolean isTrue() {
        return this == TRUE;
    }

    public boolean isFalse() {
        return this == FALSE;
    }

    public boolean isBoolean() {
        return this == TRUE || this == FALSE;
    }

    public boolean isBoth() {
        return this == BOTH;
    }

}
