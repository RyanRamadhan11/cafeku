package com.enigma.java_cafetaria.constant;

import java.util.Optional;

public enum EOrderType {
    EAT_IN,
    ONLINE,
    TAKE_AWAY;

    public static Optional<EOrderType> fromString(String value) {
        // Iterasi melalui semua nilai enumerasi ETransactionType
        for (EOrderType type : EOrderType.values()) {
            // Membandingkan nama dari setiap nilai enumerasi dengan nilai string (case-insensitive)
            if (type.name().equalsIgnoreCase(value)) {
                // Jika ada kecocokan, mengembalikan Optional yang berisi nilai enumerasi tersebut
                return Optional.of(type);
            }
        }
        // Jika tidak ada kecocokan, mengembalikan Optional kosong
        return Optional.empty();
    }
}

