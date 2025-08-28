package com.stockflow.core.util;

import com.stockflow.core.shared.domain.IdentificationType;
import com.stockflow.core.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdCardUtilsTest {

    @Test
    void validate_validIdCard_doesNotThrow() {
        assertDoesNotThrow(() -> IdCardUtils.validate("1710034065", IdentificationType.ID_CARD));
    }

    @Test
    void validate_invalidIdCard_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.validate("1710034060", IdentificationType.ID_CARD));
    }

    @Test
    void validate_validRuc_doesNotThrow() {
        assertDoesNotThrow(() -> IdCardUtils.validate("1710034065001", IdentificationType.RUC));
    }

    @Test
    void validate_invalidRuc_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.validate("1710034065000", IdentificationType.RUC));
    }

    @Test
    void initialValidation_null_throwsBusinessException() {
        assertThrows(NullPointerException.class,
                () -> IdCardUtils.initialValidation(null, 10));
    }

    @Test
    void initialValidation_empty_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.initialValidation("", 10));
    }

    @Test
    void initialValidation_nonDigits_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.initialValidation("ABC1234567", 10));
    }

    @Test
    void initialValidation_wrongLength_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.initialValidation("123456", 10));
    }

    @Test
    void verifyProvinceCode_invalid_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.verifyProvinceCode(99));
    }

    @Test
    void verifyThirdDigit_invalidIdCard_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.verifyThirdDigit(9, IdentificationType.ID_CARD));
    }

    @Test
    void verifyThirdDigit_invalidRuc_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.verifyThirdDigit(8, IdentificationType.RUC));
    }

    @Test
    void verifyEstablishmentNumber_invalid_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.verifyEstablishmentNumber(0));
    }

    @Test
    void verifyAlgorithmMod10_invalidCheckDigit_throwsBusinessException() {
        assertThrows(BusinessException.class,
                () -> IdCardUtils.verifyAlgorithmMod10("171003406", 0));
    }
}