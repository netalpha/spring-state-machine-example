// Copyright (c) 2018 Travelex Ltd

package com.statemachine.statemachinejpapersistanceexample20.enums;

import java.util.Optional;

public enum PartyType {
    CUSTOMER,
    BENEFICIARY;

    /**
     * Tries to map the provided argument to a party type.
     *
     * @param name The string representation of the party type
     * @return If the mapping is correct the party type is returned otherwise returns nothing
     */
    public static Optional<PartyType> fromString(String name) {
        for (PartyType field : PartyType.values()) {
            if (field.toString().equalsIgnoreCase(name)) {
                return Optional.of(field);
            }
        }

        return Optional.empty();
    }

}
