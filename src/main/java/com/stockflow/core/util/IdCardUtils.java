package com.stockflow.core.util;

import com.stockflow.core.shared.domain.IdentificationType;
import com.stockflow.core.shared.exception.BusinessException;

import java.util.Objects;

public abstract class IdCardUtils {

    /**
     * Validates an identification number based on its type.
     * @param identificationNumber The identification number to validate.
     * @param type The type of identification (e.g., ID_CARD, RUC).
     * @throws IllegalStateException if the identification number is invalid for the given type.
     * */
    public static void validate(String identificationNumber, IdentificationType type) {
        switch (type) {
            case ID_CARD:
                validateNationalIdentification(identificationNumber);
                break;
            case RUC:
                validateRuc(identificationNumber);
                break;
            case FINAL_CONSUMER:
            case PASSPORT:
            case FOREIGN_IDENTIFICATION:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * Validates a national identification number (cédula) according to Ecuadorian rules.
     * @param identificationNumber The 10-digit identification number to validate.
     * @throws BusinessException if the identification number is invalid.
     * */
    public static void validateNationalIdentification(String identificationNumber) {
        String[] idCard = identificationNumber.split("");
        initialValidation(identificationNumber, 10);
        verifyProvinceCode(Integer.parseInt(identificationNumber.substring(0, 2)));
        verifyThirdDigit(Integer.parseInt(idCard[2]), IdentificationType.ID_CARD);
        verifyAlgorithmMod10(identificationNumber.substring(0, 9), Integer.parseInt(idCard[9]));
    }

    /**
     * Validates a RUC (Registro Único de Contribuyentes) according to Ecuadorian rules.
     * @param identificationNumber The 13-digit RUC number to validate.
     * @throws BusinessException if the RUC number is invalid.
     */
    public static void validateRuc(String identificationNumber) {
        initialValidation(identificationNumber, 13);

        int provinceCode = Integer.parseInt(identificationNumber.substring(0, 2));
        verifyProvinceCode(provinceCode);

        int thirdDigit = Character.getNumericValue(identificationNumber.charAt(2));
        verifyThirdDigit(thirdDigit, IdentificationType.RUC);

        int establishmentNumber = Integer.parseInt(identificationNumber.substring(10, 13));
        verifyEstablishmentNumber(establishmentNumber);

        if (thirdDigit != 6 && thirdDigit != 9) {
            verifyAlgorithmMod10(identificationNumber.substring(0, 9), Character.getNumericValue(identificationNumber.charAt(9)));
        }
    }

    /**
     * Performs initial validation checks common to both ID cards and RUCs.
     * @param identificationNumber The identification number string.
     * @param expectedLength The expected length of the identification number.
     * @throws BusinessException if the initial validation fails.
     */
    public static void initialValidation(String identificationNumber, int expectedLength) {
        Objects.requireNonNull(identificationNumber, "error.idcard.null_value");

        if (identificationNumber.isEmpty()) {
            throw new BusinessException("error.idcard.empty_value");
        }

        if (!identificationNumber.matches("\\d+")) {
            throw new BusinessException("error.idcard.only_digits");
        }

        if (identificationNumber.length() != expectedLength) {
            throw new BusinessException("error.idcard.invalid_length", expectedLength);
        }
    }

    /**
     * Verifies the province code of an identification number.
     * @param provinceCode The province code (first two digits of the ID/RUC).
     * @throws BusinessException if the province code is invalid.
     */
    public static void verifyProvinceCode(int provinceCode) {
        if (provinceCode < 0 || provinceCode > 24) {
            throw new BusinessException("error.idcard.invalid_province_code");
        }
    }

    /**
     * Verifies the third digit of an identification number based on its type.
     * @param thirdDigit The third digit of the ID/RUC.
     * @param type The type of identification (ID_CARD or RUC).
     * @throws BusinessException if the third digit is invalid for the given type.
     */
    public static void verifyThirdDigit(int thirdDigit, IdentificationType type) {
        switch (type) {
            case ID_CARD:
                if (thirdDigit < 0 || thirdDigit > 5) {
                    throw new BusinessException("error.idcard.invalid_third_digit_id");
                }
                break;
            case RUC:
                if (!((thirdDigit >= 0 && thirdDigit < 7) || thirdDigit == 9)) {
                    throw new BusinessException("error.idcard.invalid_third_digit_ruc");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * Verifies the establishment number of a RUC.
     * @param establishmentNumber The last three digits of the RUC, representing the establishment number.
     * @throws BusinessException if the establishment number is invalid (e.g., 0).
     */
    public static void verifyEstablishmentNumber(int establishmentNumber) {
        if (establishmentNumber <= 0) {
            throw new BusinessException("error.idcard.invalid_establishment_number");
        }
    }

    /**
     * Verifies the check digit of an identification number using the Modulo 10 algorithm.
     * @param digits The first nine digits of the ID/RUC.
     * @param checkDigit The tenth digit (check digit) of the ID/RUC.
     * @throws BusinessException if the check digit is invalid.
     */
    public static void verifyAlgorithmMod10(String digits, int checkDigit) {
        int[] coefficients = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int total = 0;

        for (int i = 0; i < digits.length(); i++) {
            int value = Character.getNumericValue(digits.charAt(i)) * coefficients[i];
            if (value >= 10) {
                value = value / 10 + value % 10;
            }
            total += value;
        }

        int result = (total % 10 == 0) ? 0 : 10 - (total % 10);

        if (result != checkDigit) {
            throw new BusinessException("error.idcard.invalid_check_digit");
        }
    }

}