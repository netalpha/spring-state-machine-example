// Copyright (c) 2019 Travelex Ltd

package com.statemachine.statemachinejpapersistanceexample20.enums;

public enum PartyEvent {

    NEW,
//    UPDATE,

    // Sanction
//    SANCTION_CONFIRMED,
    SANCTION_PASSED,
//    SANCTION_UNDER_REVIEW,

    // PEP
//    PEP_CONFIRMED,
    PEP_PASSED,
//    PEP_UNDER_REVIEW,

    // KYC
//    KYC_PASS,
//    KYC_DOC_REQUIRED,

    // Document
//    DOCUMENT_ACCEPTED, DOCUMENT_REJECTED, DOCUMENT_PROVIDED,

//    RE_CHECK, CLOSED,
    KYC_NOT_REQUIRED, SANCTION_INPROGRESS, COMPLIANCE_UNDER_REVIEW, COMPLIANCE_PASSED,
//    SUSPENDED
}
