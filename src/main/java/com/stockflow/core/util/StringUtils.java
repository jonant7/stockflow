package com.stockflow.core.util;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

public abstract class StringUtils {

    public static void assertNotNullOrEmpty(String str, String message) {
        Assert.isTrue(!isNullOrEmpty(str), message);
    }

    public static boolean isNullOrEmpty(String str) {
        return Objects.isNull(trimToNull(str));
    }

    public static String trimToNull(String str) {
        return Optional.ofNullable(str)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);
    }

    public static Optional<String> trimToOptional(String str) {
        return Optional.ofNullable(trimToNull(str));
    }

    public static String trimToEmpty(String str) {
        return Optional.ofNullable(str)
                .map(String::trim)
                .orElse("");
    }

}