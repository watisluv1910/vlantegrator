package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.core.integrator.model.RouteNetwork;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.MapperConfig;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface DefaultMapper {

    @Named("toPublishedPortsString")
    default String toPublishedPortsString(List<Pair<@NotNull Integer, @NotNull Integer>> ports) {
        return ports.stream()
                .map(pair -> pair.getLeft() + ":" + pair.getRight())
                .collect(Collectors.joining(","));
    }

    @Named("toPublishedPortsList")
    default List<Pair<@NotNull Integer, @NotNull Integer>> toPublishedPortsList(String ports) {
        return StringUtils.isBlank(ports)
                ? Collections.emptyList()
                : Arrays.stream(ports.split(","))
                        .map(String::trim)
                        .map(it -> it.split(":"))
                        .map(it -> Pair.of(Integer.parseInt(it[0]), Integer.parseInt(it[1])))
                        .toList();
    }

    @Named("toNetworksFromNames")
    default List<RouteNetwork> toNetworksFromNames(List<String> networkNames) {
        return networkNames.stream().map(it -> new RouteNetwork(it, null)).toList();
    }
}