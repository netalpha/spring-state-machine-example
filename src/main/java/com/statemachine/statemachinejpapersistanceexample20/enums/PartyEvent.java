// Copyright (c) 2019 Travelex Ltd

package com.statemachine.statemachinejpapersistanceexample20.enums;

public enum PartyEvent {

    NEW,
//    UPDATE,
   KYC_NOT_REQUIRED,

    // Sanction
//    SANCTION_CONFIRMED,
    SANCTION_INPROGRESS,
    SANCTION_PASSED,
//    SANCTION_UNDER_REVIEW,

    // PEP
//    PEP_CONFIRMED,
    PEP_INPROGRESS,
    PEP_PASSED,
//    PEP_UNDER_REVIEW,

    // KYC
//    KYC_PASS,
//    KYC_DOC_REQUIRED,

    // Document
//    DOCUMENT_ACCEPTED, DOCUMENT_REJECTED, DOCUMENT_PROVIDED,

//    RE_CHECK, CLOSED,
     COMPLIANCE_UNDER_REVIEW, COMPLIANCE_PASSED, BANK_PASSED,
//    SUSPENDED
}
