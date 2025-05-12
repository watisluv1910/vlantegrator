package com.wladischlau.vlt.core.integrator.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum RouteAction {
    CREATE("create"),
    DELETE("delete"),
    BUILD("build"),
    DEPLOY("deploy"),
    STOP("stop"),
    RESTART("restart"),
    REMOVE("remove");

    private final String literal;

    public static Optional<RouteAction> fromLiteral(String literal) {
        return Arrays.stream(values())
                .filter(it -> it.literal.equals(literal))
                .findFirst();
    }
}
