package com.wladischlau.vlt.core.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RouteId(@JsonProperty("uuid") UUID uuid,
                      @JsonProperty("commitHash") String commitHash) {

    public String full() {
        return uuid + "." + commitHash;
    }
}
