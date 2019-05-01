// Copyright (c) 2018 Travelex Ltd

package com.statemachine.statemachinejpapersistanceexample20.enums;

import java.util.Optional;
import java.util.stream.Stream;

public enum PartyStatus {

    S0,

    INCOMPLETE,

    CHOICE_PROFILE_COMPLETE,

    COMPLETE,

    FORK_UNDER_REVIEW,
    UNDER_REVIEW,
    JOIN_UNDER_REVIEW,

    // KYC
    KYC_NOT_DONE,
    KYC_IN_PROGRESS,
    KYC_NOT_REQUIRED,
    KYC_DONE,

    // Sanction
    SANCTION_NOT_DONE,
    SANCTION_IN_PROGRESS,
    SANCTION_PASSED,
    SANCTION_DONE,

    //PEP
    PEP_NOT_DONE,
    PEP_IN_PROGRESS,
    PEP_PASSED,
    PEP_DONE,

    //saleforce
    ACTIVE,
    DELETED,
    COMPLIANCE_STATUS,

    //compliance
    COMPLIANCE_NOT_DONE, COMPLIANCE_UNDER_REVIEW, COMPLIANCE_PASSED, COMPLIANCE_DONE, BANK_NOT_DONE, BAN_IN_PROGRESS, BANK_PASSED, BANK_IN_PROGRESS, BANK_NOT_REQUIRED, BANK_DONE, CHOICE_BANK_CHECK;

    public static Optional<PartyStatus> fromString(String status) {
        return Stream.of(PartyStatus.values()).filter(t -> t.name().equalsIgnoreCase(status)).findFirst();
    }
}
