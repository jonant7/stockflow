package com.stockflow.core.util;

import java.text.Normalizer;
import java.util.Objects;

public class SlugUtils {

    public static String toSlug(String input) {
        if (Objects.isNull(input)) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\w\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }

}