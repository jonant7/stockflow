package com.stockflow.core.branch.domain;


public class BranchMother {

    private static final String CODE = "100";
    private static final String INVALID_CODE = "ABC";
    private static final String INVALID_LENGTH_CODE = "9876";
    private static final String NAME = "ACME Corporation";
    private static final String ADDRESS = "ACME Main Distribution Center";
    private static final String PHONE = "+1-505-555-DEPT";
    private static final String INVALID_PHONE = "+1-505-555-DEPOT";
    private static final String EMAIL = "info@acme-corporation.com";

    public static final String PHONE_NORMALIZED = "+15055553378";

    private static Branch baseMainBranch() {
        return Branch.builder()
                .code(CODE)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .isMain(Boolean.TRUE)
                .build();
    }

    private static Branch baseBranch() {
        return Branch.builder()
                .code(CODE)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .isMain(Boolean.FALSE)
                .build();
    }


    public static Branch activeMainBranch() {
        Branch branch = baseMainBranch();
        branch.activate();
        return branch;
    }

    public static Branch inactiveMainBranch() {
        Branch branch = baseMainBranch();
        branch.deactivate();
        return branch;
    }

    public static Branch activeBranch() {
        Branch branch = baseBranch();
        branch.activate();
        return branch;
    }

    public static Branch inactiveBranch() {
        Branch branch = baseBranch();
        branch.deactivate();
        return branch;
    }

    public static BranchPostDTO branchPostDTO() {
        return BranchPostDTO.builder()
                .code(CODE)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .isMain(Boolean.FALSE)
                .build();
    }

    public static BranchPostDTO mainBranchPostDTO() {
        return BranchPostDTO.builder()
                .code(CODE)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .isMain(Boolean.TRUE)
                .build();
    }

    public static BranchPostDTO branchPostDTOWithInvalidCode() {
        return BranchPostDTO.builder()
                .code(INVALID_CODE)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .isMain(Boolean.FALSE)
                .build();
    }

    public static BranchPostDTO branchPostDTOWithInvalidLengthCode() {
        return BranchPostDTO.builder()
                .code(INVALID_LENGTH_CODE)
                .name(NAME)
                .address(ADDRESS)
                .phone(PHONE)
                .email(EMAIL)
                .isMain(Boolean.FALSE)
                .build();
    }

    public static BranchPostDTO branchPostDTOWithoutPhone() {
        return BranchPostDTO.builder()
                .code(CODE)
                .name(NAME)
                .address(ADDRESS)
                .email(EMAIL)
                .isMain(Boolean.FALSE)
                .build();
    }

    public static BranchPostDTO branchPostDTOWithInvalidPhone() {
        return BranchPostDTO.builder()
                .code(CODE)
                .name(NAME)
                .address(ADDRESS)
                .phone(INVALID_PHONE)
                .email(EMAIL)
                .isMain(Boolean.FALSE)
                .build();
    }

    public static BranchPutDTO branchPutDTO() {
        return BranchPutDTO.builder()
                .name("Updated Corporation")
                .address("Updated Address")
                .phone("+593987654321")
                .email("updated@acme-corporation.com")
                .build();
    }

}