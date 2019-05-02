
package com.statemachine.statemachinejpapersistanceexample20.enums;


public enum Status {

    ROOT,

    S0,

    S1,

    FORK_S2,
    S2,
    JOIN_S2,

    // S21
    S21I, S21_IN_PROGRESS, S21_NOT_REQUIRED, S21E,

    // S22
    S22I, S22_IN_PROGRESS, S22_PASSED, S22E,

    // S23
    S23I, S23_IN_PROGRESS, S23_PASSED, S23E,

    // S33
    S24I, S24_PASSED, S24_IN_PROGRESS, S24_NOT_REQUIRED, S24E, CHOICE_S24,

    //
    S4, S5, S3;
}
