package com.wladischlau.vlt.core.integrator.repository;

import com.wladischlau.vlt.core.jooq.vlt_repo.Keys;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.daos.VltAdapterDao;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.daos.VltRouteDao;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.daos.VltRouteUserActionDao;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltAdapter;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNode;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeConnection;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeConnectionStyle;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodePosition;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeStyle;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRoute;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteNetwork;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteUserAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.wladischlau.vlt.core.jooq.vlt_repo.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VltRepository {

    private final DSLContext ctx;

    private final VltAdapterDao vltAdapterDao;
    private final VltRouteDao vltRouteDao;
    private final VltRouteUserActionDao vltRouteUserActionDao;

    public List<VltAdapter> findAllAdapters() {
        return vltAdapterDao.findAll();
    }

    public Optional<VltAdapter> findAdapterById(UUID id) {
        return ctx.selectFrom(VLT_ADAPTER)
                .where(VLT_ADAPTER.ID.eq(id))
                .fetchOptionalInto(VltAdapter.class);
    }

    public Optional<VltAdapter> findAdapterByName(String adapterName) {
        return ctx.selectFrom(VLT_ADAPTER)
                .where(VLT_ADAPTER.NAME.equalIgnoreCase(adapterName))
                .fetchOptionalInto(VltAdapter.class);
    }

    public void upsertAdapter(VltAdapter adapter) {
        ctx.insertInto(VLT_ADAPTER)
                .set(ctx.newRecord(VLT_ADAPTER, adapter))
                .onConflictOnConstraint(Keys.VLT_ADAPTER_NAME_KEY)
                .doUpdate()
                .set(VLT_ADAPTER.DISPLAY_NAME, adapter.displayName())
                .set(VLT_ADAPTER.DESCRIPTION, adapter.description())
                .set(VLT_ADAPTER.CLAZZ, adapter.clazz())
                .set(VLT_ADAPTER.TYPE, adapter.type())
                .set(VLT_ADAPTER.DIRECTION, adapter.direction())
                .set(VLT_ADAPTER.CHANNEL_KIND, adapter.channelKind())
                .execute();
    }

    public UUID insertRoute(VltRoute route) {
        return ctx.insertInto(VLT_ROUTE)
                .set(ctx.newRecord(VLT_ROUTE, route))
                .returningResult(VLT_ROUTE.ID)
                .fetchOne(VLT_ROUTE.ID);
    }

    public List<VltRoute> findAllRoutes() {
        return vltRouteDao.findAll();
    }

    public Optional<VltRoute> findRouteById(UUID routeId) {
        return ctx.selectFrom(VLT_ROUTE)
                .where(VLT_ROUTE.ID.eq(routeId))
                .fetchOptionalInto(VltRoute.class);
    }

    public void updateRoute(VltRoute route) {
        ctx.update(VLT_ROUTE)
                .set(VLT_ROUTE.NAME, route.name())
                .set(VLT_ROUTE.DESCRIPTION, route.description())
                .set(VLT_ROUTE.OWNER_NAME, route.ownerName())
                .set(VLT_ROUTE.ENV, route.env())
                .set(VLT_ROUTE.PUBLISHED_PORTS, route.publishedPorts())
                .where(VLT_ROUTE.ID.eq(route.id()))
                .execute();
    }

    public void updateRouteVersion(UUID id, String versionHash) {
        ctx.update(VLT_ROUTE)
                .set(VLT_ROUTE.VERSION_HASH, versionHash)
                .where(VLT_ROUTE.ID.eq(id))
                .execute();
    }

    public void deleteRoute(UUID routeId) {
        ctx.deleteFrom(VLT_ROUTE)
                .where(VLT_ROUTE.ID.eq(routeId))
                .execute();
    }

    public List<VltRouteUserAction> findAllRouteUserActions() {
        return vltRouteUserActionDao.findAll();
    }

    public List<VltRouteUserAction> findRouteUserActionsByUsername(String username) {
        return ctx.selectFrom(VLT_ROUTE_USER_ACTION)
                .where(VLT_ROUTE_USER_ACTION.USER_NAME.equalIgnoreCase(username))
                .fetchInto(VltRouteUserAction.class);
    }

    public void insertRouteUserAction(VltRouteUserAction routeUserAction) {
        ctx.insertInto(VLT_ROUTE_USER_ACTION)
                .set(ctx.newRecord(VLT_ROUTE_USER_ACTION, routeUserAction))
                .execute();
    }

    public List<VltRouteNetwork> findRouteNetworksByRouteId(UUID routeId) {
        var subStep = ctx.select(VLT_ROUTE_NETWORKS.VLT_ROUTE_NETWORK_ID)
                .from(VLT_ROUTE_NETWORKS)
                .where(VLT_ROUTE_NETWORKS.VLT_ROUTE_ID.eq(routeId));

        return ctx.select(VLT_ROUTE_NETWORK)
                .from(VLT_ROUTE_NETWORK)
                .where(VLT_ROUTE_NETWORK.ID.in(subStep))
                .fetchInto(VltRouteNetwork.class);
    }

    public List<UUID> findNetworkIdsByNames(List<String> names) {
        return ctx.select(VLT_ROUTE_NETWORK.ID)
                .from(VLT_ROUTE_NETWORK)
                .where(VLT_ROUTE_NETWORK.NAME.in(names))
                .fetch(VLT_ROUTE_NETWORK.ID);
    }

    public void addNetworksToRoute(List<UUID> networkIds, UUID routeId) {
        networkIds.forEach(networkId -> ctx.insertInto(VLT_ROUTE_NETWORKS)
                .set(VLT_ROUTE_NETWORKS.VLT_ROUTE_ID, routeId)
                .set(VLT_ROUTE_NETWORKS.VLT_ROUTE_NETWORK_ID, networkId)
                .onConflictDoNothing()
                .execute());
    }

    public void removeNetworksFromRoute(List<UUID> networkIds, UUID routeId) {
        ctx.deleteFrom(VLT_ROUTE_NETWORKS)
                .where(VLT_ROUTE_NETWORKS.VLT_ROUTE_ID.eq(routeId))
                .and(VLT_ROUTE_NETWORKS.VLT_ROUTE_NETWORK_ID.in(networkIds))
                .execute();
    }

    public void removeAllNetworksFromRoute(UUID routeId) {
        ctx.deleteFrom(VLT_ROUTE_NETWORKS)
                .where(VLT_ROUTE_NETWORKS.VLT_ROUTE_ID.eq(routeId))
                .execute();
    }

    public UUID upsertNode(VltNode node) {
        return ctx.insertInto(VLT_NODE)
                .set(ctx.newRecord(VLT_NODE, node))
                .onDuplicateKeyUpdate()
                .set(VLT_NODE.NAME, node.name())
                .set(VLT_NODE.CONFIG, node.config())
                .returning()
                .fetchOne(VLT_NODE.ID);
    }

    public void upsertNodes(List<VltNode> nodes) {
        var records = nodes.stream().map(n -> ctx.newRecord(VLT_NODE, n)).toList();

        ctx.insertInto(VLT_NODE)
                .set(records)
                .onConflictOnConstraint(Keys.VLT_NODE_PKEY)
                .doUpdate()
                // ID адаптера и маршрута для узла не могут быть изменены после создания
                .set(VLT_NODE.NAME, DSL.excluded(VLT_NODE.NAME))
                .set(VLT_NODE.CONFIG, DSL.excluded(VLT_NODE.CONFIG))
                .execute();
    }

    public List<VltNode> findNodesByRouteId(UUID routeId) {
        return ctx.selectFrom(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))
                .fetchInto(VltNode.class);
    }

    public void deleteNodesByRouteId(UUID routeId) {
        ctx.deleteFrom(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))
                .execute();
    }

    public void deleteNodesFromRouteExcluding(UUID routeId, List<UUID> toExclude) {
        ctx.deleteFrom(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))
                .and(VLT_NODE.VLT_ROUTE_ID.notIn(toExclude))
                .execute();
    }

    public void upsertNodeStyle(VltNodeStyle style) {
        ctx.insertInto(VLT_NODE_STYLE)
                .set(ctx.newRecord(VLT_NODE_STYLE, style))
                .onDuplicateKeyUpdate()
                .set(VLT_NODE_STYLE.TYPE, style.type())
                .set(VLT_NODE_STYLE.STYLE, style.style())
                .execute();
    }

    public void upsertNodeStyles(List<VltNodeStyle> styles) {
        var records = styles.stream().map(n -> ctx.newRecord(VLT_NODE_STYLE, n)).toList();

        ctx.insertInto(VLT_NODE_STYLE)
                .set(records)
                .onConflictOnConstraint(Keys.VLT_NODE_STYLE_PKEY)
                .doUpdate()
                .set(VLT_NODE_STYLE.TYPE, DSL.excluded(VLT_NODE_STYLE.TYPE))
                .set(VLT_NODE_STYLE.STYLE, DSL.excluded(VLT_NODE_STYLE.STYLE))
                .execute();
    }

    public Optional<VltNodeStyle> findNodeStyleByNodeId(UUID nodeId) {
        return ctx.selectFrom(VLT_NODE_STYLE)
                .where(VLT_NODE_STYLE.VLT_NODE_ID.eq(nodeId))
                .fetchOptionalInto(VltNodeStyle.class);
    }

    public void deleteNodeStylesByRouteId(UUID routeId) {
        ctx.deleteFrom(VLT_NODE_STYLE)
                .where(VLT_NODE_STYLE.VLT_NODE_ID.in(
                        ctx.select(VLT_NODE.ID)
                                .from(VLT_NODE)
                                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))))
                .execute();
    }

    public void deleteNodeStylesFromRouteExcluding(UUID routeId,
                                                   List<UUID> toExcludeNodesIds) {
        ctx.deleteFrom(VLT_NODE_STYLE)
                .where(VLT_NODE_STYLE.VLT_NODE_ID.in(
                        ctx.select(VLT_NODE.ID)
                                .from(VLT_NODE)
                                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))
                                .and(VLT_NODE.ID.notIn(toExcludeNodesIds))
                ))
                .execute();
    }

    public void upsertNodePosition(VltNodePosition position) {
        ctx.insertInto(VLT_NODE_POSITION)
                .set(ctx.newRecord(VLT_NODE_POSITION, position))
                .onConflictOnConstraint(Keys.VLT_NODE_POSITION_PKEY)
                .doUpdate()
                .set(VLT_NODE_POSITION.COORD_X, position.coordX())
                .set(VLT_NODE_POSITION.COORD_Y, position.coordY())
                .set(VLT_NODE_POSITION.Z_INDEX, position.zIndex())
                .execute();
    }

    public Optional<VltNodePosition> findNodePositionByNodeId(UUID nodeId) {
        return ctx.selectFrom(VLT_NODE_POSITION)
                .where(VLT_NODE_POSITION.VLT_NODE_ID.eq(nodeId))
                .fetchOptionalInto(VltNodePosition.class);
    }

    public void deleteNodePositionsByRouteId(UUID routeId) {
        ctx.deleteFrom(VLT_NODE_POSITION)
                .where(VLT_NODE_POSITION.VLT_NODE_ID.in(
                        ctx.select(VLT_NODE.ID)
                                .from(VLT_NODE)
                                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))))
                .execute();
    }

    public void deleteNodePositionsFromRouteExcluding(UUID routeId,
                                                      List<UUID> toExcludeNodesIds) {
        ctx.deleteFrom(VLT_NODE_POSITION)
                .where(VLT_NODE_POSITION.VLT_NODE_ID.in(
                        ctx.select(VLT_NODE.ID)
                                .from(VLT_NODE)
                                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))
                                .and(VLT_NODE.ID.notIn(toExcludeNodesIds)))
                )
                .execute();
    }

    public UUID insertNodeConnection(VltNodeConnection conn) {
        return ctx.insertInto(VLT_NODE_CONNECTION)
                .set(ctx.newRecord(VLT_NODE_CONNECTION, conn))
                .onDuplicateKeyIgnore()
                .returning(VLT_NODE_CONNECTION.ID)
                .fetchOne(VLT_NODE_CONNECTION.ID);
    }

    public List<VltNodeConnection> findNodeConnectionsByRouteId(UUID routeId) {
        var subStep = ctx.select(VLT_NODE.ID)
                .from(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId));

        return ctx.selectDistinct(VLT_NODE_CONNECTION)
                .from(VLT_NODE_CONNECTION)
                .where(VLT_NODE_CONNECTION.SOURCE_ID.in(subStep))
                .or(VLT_NODE_CONNECTION.TARGET_ID.in(subStep))
                .fetchInto(VltNodeConnection.class);
    }

    public void deleteNodeConnectionsByRouteId(UUID routeId) {
        var subStep = ctx.select(VLT_NODE.ID)
                .from(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId));

        ctx.deleteFrom(VLT_NODE_CONNECTION)
                .where(VLT_NODE_CONNECTION.SOURCE_ID.in(subStep))
                .or(VLT_NODE_CONNECTION.TARGET_ID.in(subStep))
                .execute();
    }

    public void deleteNodeConnectionsFromRouteExcludingNodes(UUID routeId,
                                                             List<UUID> toExcludeNodesIds) {
        var subStep = ctx.select(VLT_NODE.ID)
                .from(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))
                .and(VLT_NODE.ID.notIn(toExcludeNodesIds));

        ctx.deleteFrom(VLT_NODE_CONNECTION)
                .where(VLT_NODE_CONNECTION.SOURCE_ID.in(subStep))
                .or(VLT_NODE_CONNECTION.TARGET_ID.in(subStep))
                .execute();
    }

    public void upsertNodeConnectionStyle(VltNodeConnectionStyle style) {
        ctx.insertInto(VLT_NODE_CONNECTION_STYLE)
                .set(ctx.newRecord(VLT_NODE_CONNECTION_STYLE, style))
                .onConflictOnConstraint(Keys.VLT_NODE_CONNECTION_STYLE_PKEY)
                .doUpdate()
                .set(VLT_NODE_CONNECTION_STYLE.TYPE, style.type())
                .set(VLT_NODE_CONNECTION_STYLE.MARKER_START_TYPE, style.markerStartType())
                .set(VLT_NODE_CONNECTION_STYLE.MARKER_END_TYPE, style.markerEndType())
                .set(VLT_NODE_CONNECTION_STYLE.ANIMATED, style.animated())
                .set(VLT_NODE_CONNECTION_STYLE.FOCUSABLE, style.focusable())
                .execute();
    }

    public Optional<VltNodeConnectionStyle> findNodeConnectionStyleByConnectionId(UUID connectionId) {
        return ctx.selectFrom(VLT_NODE_CONNECTION_STYLE)
                .where(VLT_NODE_CONNECTION_STYLE.VLT_NODE_CONNECTION_ID.eq(connectionId))
                .fetchOptionalInto(VltNodeConnectionStyle.class);
    }

    public void deleteNodeConnectionStylesByRouteId(UUID routeId) {
        var subStep = ctx.select(VLT_NODE.ID)
                .from(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId));

        ctx.deleteFrom(VLT_NODE_CONNECTION_STYLE)
                .where(VLT_NODE_CONNECTION_STYLE.VLT_NODE_CONNECTION_ID.in(
                        ctx.select(VLT_NODE_CONNECTION.ID)
                                .from(VLT_NODE_CONNECTION)
                                .where(VLT_NODE_CONNECTION.SOURCE_ID.in(subStep))
                                .or(VLT_NODE_CONNECTION.TARGET_ID.in(subStep))))
                .execute();
    }

    public void deleteNodeConnectionStylesFromRouteExcludingNodes(UUID routeId,
                                                                  List<UUID> toExcludeNodeIds) {
        var subStep = ctx.select(VLT_NODE.ID)
                .from(VLT_NODE)
                .where(VLT_NODE.VLT_ROUTE_ID.eq(routeId))
                .and(VLT_NODE.ID.notIn(toExcludeNodeIds));

        ctx.deleteFrom(VLT_NODE_CONNECTION_STYLE)
                .where(VLT_NODE_CONNECTION_STYLE.VLT_NODE_CONNECTION_ID.in(
                        ctx.select(VLT_NODE_CONNECTION.ID)
                                .from(VLT_NODE_CONNECTION)
                                .where(VLT_NODE_CONNECTION.SOURCE_ID.in(subStep))
                                .or(VLT_NODE_CONNECTION.TARGET_ID.in(subStep))))
                .execute();
    }
}
