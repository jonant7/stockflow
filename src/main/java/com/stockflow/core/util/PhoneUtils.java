package com.stockflow.core.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.stockflow.core.shared.exception.BusinessException;

import java.util.Objects;

public final class PhoneUtils {

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    private PhoneUtils() {}

    /**
     * Normalizes and validates a telephone number to E.164 format.
     * @param input input number (may contain spaces, hyphens, parentheses, etc.)
     * @param defaultRegion default region in ISO 3166-1 alpha-2 format (e.g., “EC” for Ecuador).
     * @return number formatted in E.164 (e.g. +593987654321).
     * @throws BusinessException() if it is not a valid number.
     */
    public static String toE164(String input, String defaultRegion) {
        if (Objects.isNull(input) || input.isBlank()) {
            throw new BusinessException("error.phone.null_or_empty");
        }

        try {
            PhoneNumber number = PHONE_UTIL.parse(input, defaultRegion);

            if (!PHONE_UTIL.isValidNumber(number)) {
                throw new BusinessException("error.phone.invalid_number");
            }

            return PHONE_UTIL.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException ex) {
            throw new BusinessException("error.phone.parse_error", ex, ex.getMessage());
        }
    }

    /**
     * Check if a number is valid (possible and correctly formatted) for the region.
     */
    public static boolean isValid(String input, String defaultRegion) {
        try {
            PhoneNumber number = PHONE_UTIL.parse(input, defaultRegion);
            return PHONE_UTIL.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }
}