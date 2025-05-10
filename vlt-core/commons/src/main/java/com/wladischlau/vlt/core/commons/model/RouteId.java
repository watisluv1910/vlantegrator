package com.wladischlau.vlt.core.commons.model;

import java.util.UUID;

public record RouteId(UUID id, String versionHash) {

    public String full() {
        return id + "." + versionHash;
    }
}
