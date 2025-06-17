package com.wladischlau.vlt.core.commons.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ContainerStatus {
    RUNNING("running"),
    EXITED("exited"),
    PAUSED("paused"),
    RESTARTING("restarting"),
    DEAD("dead"),
    CREATED("created"),
    UNDEFINED("undefined");

    private final String literal;

    public static ContainerStatus fromLiteral(String literal) {
        for (var status : ContainerStatus.values()) {
            if (status.literal.equals(literal)) {
                return status;
            }
        }

        return null;
    }
}