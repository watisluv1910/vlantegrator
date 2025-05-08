package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.integrator.model.RouteId;
import com.wladischlau.vlt.core.integrator.rest.dto.AdapterDto;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteIdDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = DefaultMapper.class, uses = {DefaultMapper.class})
public interface DtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "className", source = "clazz")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", expression = "java(src.direction().name())")
    @Mapping(target = "channelKind", expression = "java(src.channelKind().name())")
    AdapterDto toDto(Adapter src);

    @Mapping(target = "routeId", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "owner", source = "checkedOwner")
    @Mapping(target = "publishedPorts", source = "request.publishedPorts", qualifiedByName = "toPublishedPortsList")
    @Mapping(target = "networks", source = "request.networks", qualifiedByName = "toNetworksFromNames")
    @Mapping(target = "env", source = "request.env")
    Route fromDto(CreateRouteRequestDto request, String checkedOwner);

    @Mapping(target = "routeId", source = "routeId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "owner", source = "ownerName")
    @Mapping(target = "publishedPorts", source = "publishedPorts", qualifiedByName = "toPublishedPortsList")
    @Mapping(target = "networks", source = "networks", qualifiedByName = "toNetworksFromNames")
    @Mapping(target = "env", source = "env")
    Route fromDto(RouteDto src);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionHash", source = "versionHash")
    RouteIdDto toDto(RouteId src);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionHash", source = "versionHash")
    RouteId fromDto(RouteIdDto src);
}
