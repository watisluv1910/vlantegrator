package com.wladischlau.vlt.adapters.common;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum AdapterType {
    HTTP_INBOUND("http", Direction.INBOUND, ChannelKind.CHANNEL),
    HTTP_OUTBOUND("http", Direction.OUTBOUND, ChannelKind.GATEWAY),
    JDBC_OUTBOUND("jdbc", Direction.OUTBOUND, ChannelKind.GATEWAY),
    SPEL_TRANSFORMER("spelTransformer", Direction.COMMON, ChannelKind.NONE),
    LOGGER("logger", Direction.COMMON, ChannelKind.NONE),
    DIVIDER("divider", Direction.COMMON, ChannelKind.NONE);

    private final String type;
    private final String name;
    private final Direction direction;
    private final ChannelKind channelKind;

    AdapterType(String type, Direction direction, ChannelKind channelKind) {
        this.type = type;
        this.direction = direction;
        this.channelKind = channelKind;
        this.name = formName(type, direction, channelKind);
    }

    public String toAdapterClassName() {
        return formName(type, direction, channelKind) + "Adapter";
    }

    public static Optional<AdapterType> fromName(String name) {
        return Arrays.stream(AdapterType.values()).filter(it -> it.name.equals(name)).findFirst();
    }

    private static String formName(String type, Direction direction, ChannelKind channelKind) {
        String dir = direction != Direction.COMMON ? StringUtils.capitalize(direction.toString().toLowerCase()) : "";
        String ck = channelKind != ChannelKind.NONE ? StringUtils.capitalize(channelKind.toString().toLowerCase()) : "";
        return String.format("%s%s%s", type, dir, ck);
    }

    public enum Direction {
        OUTBOUND,
        INBOUND,
        COMMON
    }

    public enum ChannelKind {
        CHANNEL,
        GATEWAY,
        NONE
    }
}