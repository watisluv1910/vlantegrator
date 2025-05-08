package com.wladischlau.vlt.core.integrator.repository;

import com.wladischlau.vlt.core.jooq.vlt_repo.Keys;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.VltNodeStyle;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.daos.VltAdapterDao;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltAdapter;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNode;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeConnection;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodeConnectionStyle;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltNodePosition;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRoute;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteNetwork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
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

    public Optional<VltRoute> findRouteByIdAndVersionHash(UUID routeId, String versionHash) {
        return ctx.selectFrom(VLT_ROUTE)
                .where(VLT_ROUTE.ID.eq(routeId))
                .and(VLT_ROUTE.VERSION_HASH.eq(versionHash))
                .fetchOptionalInto(VltRoute.class);
    }

    public UUID insertRoute(VltRoute route) {
        return ctx.insertInto(VLT_ROUTE)
                .set(ctx.newRecord(VLT_ROUTE, route))
                .returningResult(VLT_ROUTE.ID)
                .fetchOne(VLT_ROUTE.ID);
    }

    public void updateRoute(VltRoute route) {
        ctx.update(VLT_ROUTE)
                .set(ctx.newRecord(VLT_ROUTE, route))
                .where(VLT_ROUTE.ID.eq(route.id()))
                .execute();
    }

    public void deleteRoute(UUID routeId) {
        ctx.deleteFrom(VLT_ROUTE)
                .where(VLT_ROUTE.ID.eq(routeId))
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
}
