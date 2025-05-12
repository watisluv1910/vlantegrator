package com.wladischlau.vlt.core.integrator.model;

import com.wladischlau.vlt.core.commons.model.DeployActionType;
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
    START("start"),
    STOP("stop"),
    RESTART("restart"),
    REMOVE("remove");

    private final String literal;

    public static Optional<RouteAction> fromLiteral(String literal) {
        return Arrays.stream(values())
                .filter(it -> it.literal.equals(literal))
                .findFirst();
    }

    public static RouteAction fromDeployRequestType(DeployActionType deployActionType) {
        return switch (deployActionType) {
            case DeployActionType.START -> RouteAction.START;
            case DeployActionType.STOP -> RouteAction.STOP;
            case DeployActionType.RESTART -> RouteAction.RESTART;
            case DeployActionType.DELETE -> RouteAction.DELETE;
        };
    }
}
