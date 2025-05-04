package com.wladischlau.vlt.adapters.common;

import com.wladischlau.vlt.adapters.utils.Constants;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum AdapterType {
    HTTP_INBOUND("http", Direction.INBOUND, ChannelKind.CHANNEL, "HTTP-вход"),
    HTTP_OUTBOUND("http", Direction.OUTBOUND, ChannelKind.GATEWAY, "HTTP-выход"),
    JDBC_OUTBOUND("jdbc", Direction.OUTBOUND, ChannelKind.GATEWAY, "JDBC-выход"),
    SPEL_TRANSFORMER("spELTransformer", Direction.COMMON, ChannelKind.NONE, "SpEL-преобразователь"),
    LOGGER("logger", Direction.COMMON, ChannelKind.NONE, "Логгер"),
    DIVIDER("divider", Direction.COMMON, ChannelKind.NONE, "Разделитель");

    private final String type;
    private final Direction direction;
    private final ChannelKind channelKind;
    private final String name;
    private final String displayName;

    AdapterType(String type, Direction direction, ChannelKind channelKind, String displayName) {
        this.type = type;
        this.direction = direction;
        this.channelKind = channelKind;
        this.name = formName(type, direction, channelKind);
        this.displayName = displayName;
    }

    public String toAdapterClassName() {
        return Constants.ADAPTERS_BASE_PACKAGE + "."
                + StringUtils.capitalize(formName(type, direction, channelKind)) + "Adapter";
    }

    public static Optional<AdapterType> fromName(String name) {
        return Arrays.stream(AdapterType.values()).filter(it -> it.name.equalsIgnoreCase(name)).findFirst();
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