package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.core.commons.dto.DeployRequestDto;
import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import com.wladischlau.vlt.core.commons.model.DeployActionType;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.Connection;
import com.wladischlau.vlt.core.integrator.model.ConnectionFullData;
import com.wladischlau.vlt.core.integrator.model.ConnectionStyle;
import com.wladischlau.vlt.core.integrator.model.DockerNetwork;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.model.NodeFullData;
import com.wladischlau.vlt.core.integrator.model.NodePosition;
import com.wladischlau.vlt.core.integrator.model.NodeStyle;
import com.wladischlau.vlt.core.integrator.model.PlatformBasicHealth;
import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.integrator.model.RouteCacheData;
import com.wladischlau.vlt.core.integrator.model.RouteUserAction;
import com.wladischlau.vlt.core.integrator.model.UserSettings;
import com.wladischlau.vlt.core.integrator.rest.dto.AdapterDto;
import com.wladischlau.vlt.core.integrator.rest.dto.ConnectionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.ConnectionStyleDto;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.DockerNetworkDto;
import com.wladischlau.vlt.core.integrator.rest.dto.NodeDto;
import com.wladischlau.vlt.core.integrator.rest.dto.NodePositionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.NodeStyleDto;
import com.wladischlau.vlt.core.integrator.rest.dto.PlatformBasicHealthDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDefinitionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteUserActionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.UpdateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.UserAccessibilitySettingsDto;
import com.wladischlau.vlt.core.integrator.rest.dto.UserEditorSettingsDto;
import com.wladischlau.vlt.core.integrator.rest.dto.UserSettingsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

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
    @Mapping(target = "networks", source = "request.networks")
    @Mapping(target = "env", source = "request.env")
    Route fromDto(CreateRouteRequestDto request, String checkedOwner);

    @Mapping(target = "routeId", expression = "java(new RouteId(id, null))")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "owner", source = "request.ownerName")
    @Mapping(target = "publishedPorts", source = "request.publishedPorts", qualifiedByName = "toPublishedPortsList")
    @Mapping(target = "networks", source = "request.networks")
    @Mapping(target = "env", source = "request.env")
    Route fromDto(UpdateRouteRequestDto request, UUID id);

    @Mapping(target = "routeId", source = "routeId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "owner", source = "ownerName")
    @Mapping(target = "publishedPorts", source = "publishedPorts", qualifiedByName = "toPublishedPortsList")
    @Mapping(target = "networks", source = "networks")
    @Mapping(target = "env", source = "env")
    Route fromDto(RouteDto src);

    @Mapping(target = "routeId", source = "routeId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "ownerName", source = "owner")
    @Mapping(target = "publishedPorts", source = "publishedPorts", qualifiedByName = "toPublishedPortsString")
    @Mapping(target = "networks", source = "networks")
    @Mapping(target = "env", source = "env")
    RouteDto toDto(Route src);

    List<RouteDto> toRouteDto(List<Route> src);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionHash", source = "versionHash")
    RouteId fromDto(RouteIdDto src);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionHash", source = "versionHash")
    RouteIdDto toDto(RouteId src);

    @Mapping(target = "nodes", source = "cache.nodes")
    @Mapping(target = "connections", source = "cache.connections")
    RouteDefinitionDto toDto(RouteCacheData cache);

    @Mapping(target = "routeId", source = "routeId")
    @Mapping(target = "userName", source = "username")
    @Mapping(target = "userDisplayName", source = "userDisplayName")
    @Mapping(target = "action", expression = "java(src.action().getLiteral())")
    @Mapping(target = "attemptedAt", source = "attemptedAt")
    RouteUserActionDto toDto(RouteUserAction src);

    List<RouteUserActionDto> toDtoFromRouteUserAction(List<RouteUserAction> src);

    @Mapping(target = "node", expression = "java(fromDtoToNode(src, adapter))")
    @Mapping(target = "style", source = "src.style")
    @Mapping(target = "position", source = "src.position")
    NodeFullData fromDto(NodeDto src, Adapter adapter);

    @Mapping(target = "id", source = "node.id")
    @Mapping(target = "name", source = "node.name")
    @Mapping(target = "adapterId", source = "node.adapter.id")
    @Mapping(target = "config", source = "node.config")
    @Mapping(target = "style", source = "style")
    @Mapping(target = "position", source = "position")
    NodeDto toDto(NodeFullData src);

    List<NodeDto> toDtoFromNodesFullData(List<NodeFullData> nodes);

    @Mapping(target = "id", source = "src.id")
    @Mapping(target = "name", source = "src.name")
    @Mapping(target = "adapter", source = "adapter")
    @Mapping(target = "config", source = "src.config")
    Node fromDtoToNode(NodeDto src, Adapter adapter);

    @Mapping(target = "role", source = "type")
    @Mapping(target = "style", source = "config")
    NodeStyle fromDto(NodeStyleDto src);

    @Mapping(target = "type", source = "role")
    @Mapping(target = "config", source = "style")
    NodeStyleDto toDto(NodeStyle src);

    @Mapping(target = "x", source = "x")
    @Mapping(target = "y", source = "y")
    @Mapping(target = "zIndex", source = "zIndex")
    NodePosition fromDto(NodePositionDto src);

    @Mapping(target = "x", source = "x")
    @Mapping(target = "y", source = "y")
    @Mapping(target = "zIndex", source = "zIndex")
    NodePositionDto toDto(NodePosition src);

    @Mapping(target = "fromNodeId", source = "sourceId")
    @Mapping(target = "toNodeId", source = "targetId")
    Connection fromDto(ConnectionDto src);

    @Mapping(target = "sourceId", source = "connection.fromNodeId")
    @Mapping(target = "targetId", source = "connection.toNodeId")
    @Mapping(target = "style", source = "style")
    ConnectionDto toDto(Connection connection, ConnectionStyle style);

    @Mapping(target = "connection", expression = "java(fromDto(src))")
    @Mapping(target = "style", source = "style")
    ConnectionFullData fromDtoToConnectionFullData(ConnectionDto src);

    List<ConnectionFullData> fromDtoToConnectionsFullData(List<ConnectionDto> src);

    @Mapping(target = "sourceId", source = "connection.fromNodeId")
    @Mapping(target = "targetId", source = "connection.toNodeId")
    @Mapping(target = "style", source = "style")
    ConnectionDto toDtoFromConnectionFullData(ConnectionFullData src);

    List<ConnectionDto> toDtoFromConnectionsFullData(List<ConnectionFullData> src);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "startMarkerType", source = "startMarkerType")
    @Mapping(target = "endMarkerType", source = "endMarkerType")
    @Mapping(target = "animated", source = "animated")
    @Mapping(target = "focusable", source = "focusable")
    ConnectionStyle fromDto(ConnectionStyleDto src);

    @Mapping(target = "routeId", source = "route.routeId")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "env", source = "route.env")
    @Mapping(target = "ports", source = "route.publishedPorts", qualifiedByName = "toPublishedPortsString")
    @Mapping(target = "networks", source = "route.networks", qualifiedByName = "toNamesFromNetworks")
    DeployRequestDto toDto(Route route, DeployActionType action);

    @Mapping(target = "cpuPercent", source = "cpuPercent")
    @Mapping(target = "memUsedBytes", source = "memUsed")
    @Mapping(target = "memTotalBytes", source = "memTotal")
    @Mapping(target = "dbStatus", source = "dbStatus")
    @Mapping(target = "kafkaStatus", source = "kafkaStatus")
    PlatformBasicHealthDto toDto(PlatformBasicHealth src);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "driver", source = "driver")
    DockerNetworkDto toDto(DockerNetwork src);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "driver", source = "driver")
    DockerNetwork fromDto(DockerNetworkDto src);

    List<DockerNetwork> fromDtoToDockerNetworks(List<DockerNetworkDto> src);

    List<DockerNetworkDto> toDtoFromDockerNetwork(List<DockerNetwork> src);

    @Mapping(target = "editor", source = "editorSettings")
    @Mapping(target = "accessibility", source = "accessibilitySettings")
    UserSettingsDto toDto(UserSettings src);

    @Mapping(target = "editorSettings", source = "editor")
    @Mapping(target = "accessibilitySettings", source = "accessibility")
    UserSettings fromDto(UserSettingsDto src);

    @Mapping(target = "showGrid", source = "showGrid")
    @Mapping(target = "defaultViewportPosition", source = "defaultViewportPosition")
    @Mapping(target = "autosaveIntervalMs", source = "autosaveIntervalMs")
    UserEditorSettingsDto toUserEditorSettingsDto(UserSettings.EditorSettings src);

    @Mapping(target = "showGrid", source = "showGrid")
    @Mapping(target = "defaultViewportPosition", source = "defaultViewportPosition")
    @Mapping(target = "autosaveIntervalMs", source = "autosaveIntervalMs")
    UserSettings.EditorSettings fromUserEditorSettingsDto(UserEditorSettingsDto src);

    @Mapping(target = "disableAnimations", source = "disableAnimations")
    @Mapping(target = "enableHighContrast", source = "enableHighContrast")
    UserAccessibilitySettingsDto toUserAccessibilitySettingsDto(UserSettings.AccessibilitySettings src);

    @Mapping(target = "disableAnimations", source = "disableAnimations")
    @Mapping(target = "enableHighContrast", source = "enableHighContrast")
    UserSettings.AccessibilitySettings fromUserAccessibilitySettingsDto(UserSettings.AccessibilitySettings src);
}
