package com.stockflow.core.util;

import com.stockflow.core.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneUtilsTest {

    @Test
    void toE164_validNumber_returnsFormatted() {
        String result = PhoneUtils.toE164("0987654321", "EC");
        assertTrue(result.startsWith("+593")); // Ecuador prefix
    }

    @Test
    void toE164_null_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> PhoneUtils.toE164(null, "EC"));
        assertEquals("error.phone.null_or_empty", ex.getMessage());
    }

    @Test
    void toE164_blank_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> PhoneUtils.toE164("   ", "EC"));
        assertEquals("error.phone.null_or_empty", ex.getMessage());
    }

    @Test
    void toE164_invalidNumber_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> PhoneUtils.toE164("12345", "EC"));
        assertEquals("error.phone.invalid_number", ex.getMessage());
    }

    @Test
    void toE164_parseError_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> PhoneUtils.toE164("+++", "EC"));
    }

    @Test
    void isValid_validNumber_returnsTrue() {
        assertTrue(PhoneUtils.isValid("0987654321", "EC"));
    }

    @Test
    void isValid_invalidNumber_returnsFalse() {
        assertFalse(PhoneUtils.isValid("12345", "EC"));
    }

    @Test
    void isValid_parseError_returnsFalse() {
        assertFalse(PhoneUtils.isValid("+++", "EC"));
    }

}