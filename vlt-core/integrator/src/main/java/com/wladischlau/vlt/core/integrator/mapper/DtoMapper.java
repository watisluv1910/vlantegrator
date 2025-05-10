package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.ConnectionFullData;
import com.wladischlau.vlt.core.integrator.model.ConnectionStyle;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.model.NodeFullData;
import com.wladischlau.vlt.core.integrator.model.NodePosition;
import com.wladischlau.vlt.core.integrator.model.NodeStyle;
import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.integrator.rest.dto.AdapterDto;
import com.wladischlau.vlt.core.integrator.rest.dto.ConnectionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.ConnectionStyleDto;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.NodeDto;
import com.wladischlau.vlt.core.integrator.rest.dto.NodePositionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.NodeStyleDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

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

    @Mapping(target = "node", expression = "java(fromDtoToNode(src, adapter))")
    @Mapping(target = "style", source = "src.style")
    @Mapping(target = "position", source = "src.position")
    NodeFullData fromDto(NodeDto src, Adapter adapter);

    @Mapping(target = "id", source = "src.id")
    @Mapping(target = "name", source = "src.name")
    @Mapping(target = "adapter", source = "adapter")
    @Mapping(target = "config", source = "src.config")
    Node fromDtoToNode(NodeDto src, Adapter adapter);

    @Mapping(target = "role", source = "type")
    @Mapping(target = "style", source = "config")
    NodeStyle fromDto(NodeStyleDto src);

    @Mapping(target = "x", source = "x")
    @Mapping(target = "y", source = "y")
    @Mapping(target = "zIndex", source = "zIndex")
    NodePosition fromDto(NodePositionDto src);

    @Mapping(target = "connection.fromNodeId", source = "sourceId")
    @Mapping(target = "connection.toNodeId", source = "targetId")
    @Mapping(target = "style", source = "style")
    ConnectionFullData fromDto(ConnectionDto src);

    List<ConnectionFullData> fromDtoToConnectionsFullData(List<ConnectionDto> src);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "startMarkerType", source = "startMarkerType")
    @Mapping(target = "endMarkerType", source = "endMarkerType")
    @Mapping(target = "animated", source = "animated")
    @Mapping(target = "focusable", source = "focusable")
    ConnectionStyle fromDto(ConnectionStyleDto src);
}
