package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.Connection;
import com.wladischlau.vlt.core.integrator.model.ConnectionFullData;
import com.wladischlau.vlt.core.integrator.model.ConnectionStyle;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.model.NodeFullData;
import com.wladischlau.vlt.core.integrator.model.NodePosition;
import com.wladischlau.vlt.core.integrator.model.NodeStyle;
import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.integrator.model.RouteNetwork;
import com.wladischlau.vlt.core.jooq.vlt_repo.enums.AdapterDirection;
import com.wladischlau.vlt.core.jooq.vlt_repo.enums.ChannelKind;
import com.wladischlau.vlt.core.jooq.vlt_repo.enums.NetworkDriver;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltAdapter;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNode;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeConnection;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeConnectionStyle;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodePosition;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeStyle;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRoute;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteNetwork;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(config = DefaultMapper.class,
        uses = {DefaultMapper.class},
        imports = {ChannelKind.class, AdapterDirection.class, NetworkDriver.class, Class.class})
public interface ModelMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "clazz", source = "clazz")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", expression = "java(AdapterDirection.lookupLiteral(src.direction().name()))")
    @Mapping(target = "channelKind", expression = "java(ChannelKind.lookupLiteral(src.channelKind().name()))")
    VltAdapter toJooq(Adapter src);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "clazz", source = "clazz")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", expression = "java(AdapterType.Direction.valueOf(src.direction().getLiteral()))")
    @Mapping(target = "channelKind", expression = "java(AdapterType.ChannelKind.valueOf(src.channelKind().getLiteral()))")
    Adapter toModel(VltAdapter src);

    List<Adapter> toAdaptersFromJooq(List<VltAdapter> src);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "clazz", expression = "java(src.toAdapterClassName())")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", source = "direction")
    @Mapping(target = "channelKind", source = "channelKind")
    Adapter toModel(AdapterType src);

    List<Adapter> toAdaptersFromType(List<AdapterType> src);

    @Mapping(target = "routeId", source = "route", qualifiedByName = "toRouteId")
    @Mapping(target = "name", source = "route.name")
    @Mapping(target = "description", source = "route.description")
    @Mapping(target = "owner", source = "route.ownerName")
    @Mapping(target = "publishedPorts", source = "route.publishedPorts", qualifiedByName = "toPublishedPortsList")
    @Mapping(target = "env", source = "route.env")
    @Mapping(target = "networks", source = "networks")
    Route toModel(VltRoute route, List<VltRouteNetwork> networks);

    @Named("toRouteId")
    default RouteId toRouteId(VltRoute route) {
        return new RouteId(route.id(), route.versionHash());
    }

    @Mapping(target = "id", source = "routeId.id")
    @Mapping(target = "versionHash", source = "routeId.versionHash")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "ownerName", source = "owner")
    @Mapping(target = "publishedPorts", source = "publishedPorts", qualifiedByName = "toPublishedPortsString")
    @Mapping(target = "env", source = "env")
    VltRoute toJooq(Route src);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "driver", expression = "java(src.driver().getLiteral())")
    RouteNetwork toModel(VltRouteNetwork src);

    List<RouteNetwork> toRouteNetworksFromJooq(List<VltRouteNetwork> src);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "driver", expression = "java(NetworkDriver.lookupLiteral(src.driver()))")
    VltRouteNetwork toJooq(RouteNetwork src);

    @Mapping(target = "id", source = "node.id")
    @Mapping(target = "name", source = "node.name")
    @Mapping(target = "adapter", source = "adapter")
    @Mapping(target = "config", source = "node.config")
    Node toModel(VltNode node, VltAdapter adapter);

    @Mapping(target = "id", source = "node.id")
    @Mapping(target = "name", source = "node.name")
    @Mapping(target = "vltRouteId", source = "routeId")
    @Mapping(target = "vltAdapterId", source = "node.adapter.id")
    @Mapping(target = "config", source = "node.config")
    VltNode toJooq(Node node, UUID routeId);

    @Mapping(target = "role", expression = "java(src.type().getLiteral())")
    @Mapping(target = "style")
    NodeStyle toModel(VltNodeStyle src);

    @Mapping(target = "vltNodeId", source = "nodeId")
    @Mapping(target = "type", expression = "java(NodeRole.lookupLiteral(style.role()))")
    @Mapping(target = "style", source = "style.style")
    VltNodeStyle toJooq(NodeStyle style, UUID nodeId);

    @Mapping(target = "x", source = "coordX")
    @Mapping(target = "y", source = "coordY")
    @Mapping(target = "zIndex", source = "zIndex")
    NodePosition toModel(VltNodePosition src);

    @Mapping(target = "vltNodeId", source = "nodeId")
    @Mapping(target = "coordX", source = "position.x")
    @Mapping(target = "coordY", source = "position.y")
    @Mapping(target = "zIndex", source = "position.zIndex")
    VltNodePosition toJooq(NodePosition position, UUID nodeId);

    default NodeFullData toNodeFullData(VltNode node, VltAdapter adapter, VltNodeStyle style, VltNodePosition position) {
        var node_ = toModel(node, adapter);
        var style_ = toModel(style);
        var position_ = toModel(position);
        return new NodeFullData(node_, style_, position_);
    }

    @Mapping(target = "fromNodeId", source = "sourceId")
    @Mapping(target = "toNodeId", source = "targetId")
    Connection toModel(VltNodeConnection src);

    List<Connection> toConnectionsFromJooq(List<VltNodeConnection> src);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sourceId", source = "fromNodeId")
    @Mapping(target = "targetId", source = "toNodeId")
    VltNodeConnection toJooq(Connection src);

    @Mapping(target = "type", expression = "java(src.type().getLiteral())")
    @Mapping(target = "startMarkerType", expression = "java(src.markerStartType().getLiteral())")
    @Mapping(target = "endMarkerType", expression = "java(src.markerEndType().getLiteral())")
    @Mapping(target = "animated", source = "animated")
    @Mapping(target = "focusable", source = "focusable")
    ConnectionStyle toModel(VltNodeConnectionStyle src);

    @Mapping(target = "vltNodeConnectionId", source = "connectionId")
    @Mapping(target = "type", expression = "java(EdgeType.lookupLiteral(style.type()))")
    @Mapping(target = "markerStartType", expression = "java(MarkerType.lookupLiteral(style.startMarkerType()))")
    @Mapping(target = "markerEndType", expression = "java(MarkerType.lookupLiteral(style.endMarkerType()))")
    @Mapping(target = "animated", source = "style.animated")
    @Mapping(target = "focusable", source = "style.focusable")
    VltNodeConnectionStyle toJooq(ConnectionStyle style, UUID connectionId);

    @Named("toConnectionFullData")
    default ConnectionFullData toConnectionFullData(VltNodeConnection connection, VltNodeConnectionStyle style) {
        var connection_ = toModel(connection);
        var style_ = toModel(style);
        return new ConnectionFullData(connection_, style_);
    }
}
