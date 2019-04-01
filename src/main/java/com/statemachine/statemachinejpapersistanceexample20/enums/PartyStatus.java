// Copyright (c) 2018 Travelex Ltd

package com.statemachine.statemachinejpapersistanceexample20.enums;

import java.util.Optional;
import java.util.stream.Stream;

public enum PartyStatus {

    INCOMPLETE, COMPLETE,

    FORK_UNDER_REVIEW,
    UNDER_REVIEW,
    JOIN_UNDER_REVIEW,

    // KYC
    KYC_NOT_DONE,
//    KYC_IN_PROGRESS,
//    KYC_WAITING_FOR_DOCUMENTS,
    KYC_UNDER_REVIEW,
    KYC_COMPLETED,
    KYC_NOT_REQUIRED,

    // Sanction
    SANCTION_NOT_DONE,
    SANCTION_IN_PROGRESS,
//    SANCTION_UNCONFIRMED,
//    SANCTION_CONFIRMED,
//    SANCTION_WAIVED,
    SANCTION_PASSED,

    //PEP
    PEP_NOT_DONE,
    PEP_IN_PROGRESS,
//    PEP_CONFIRMED,
//    PEP_WAIVED,
//    PEP_UNCONFIRMED,
    PEP_PASSED,

//    DELETED,
    ACTIVE,

    // Salesforce
//    CLOSED,
//    SUSPENDED
    COMPLIANCE_STATUS, COMPLIANCE_NOT_DONE, COMPLIANCE_UNDER_REVIEW, COMPLIANCE_PASSED, COMPLIANCE_DONE, S0, LIVE;

    public static Optional<PartyStatus> fromString(String status) {
        return Stream.of(PartyStatus.values()).filter(t -> t.name().equalsIgnoreCase(status)).findFirst();
    }
}
