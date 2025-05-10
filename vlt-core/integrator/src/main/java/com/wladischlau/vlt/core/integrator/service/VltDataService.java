package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.config.properties.VltProperties;
import com.wladischlau.vlt.core.integrator.mapper.ModelMapper;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.Connection;
import com.wladischlau.vlt.core.integrator.model.ConnectionFullData;
import com.wladischlau.vlt.core.integrator.model.ConnectionStyle;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.model.NodeFullData;
import com.wladischlau.vlt.core.integrator.model.NodePosition;
import com.wladischlau.vlt.core.integrator.model.NodeStyle;
import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.integrator.model.RouteCacheData;
import com.wladischlau.vlt.core.integrator.model.RouteDefinition;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.integrator.model.RouteNetwork;
import com.wladischlau.vlt.core.integrator.repository.VltRepository;
import com.wladischlau.vlt.core.integrator.utils.VersionBucket;
import com.wladischlau.vlt.core.integrator.utils.VersionHashGenerator;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteNetwork;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("SpringTransactionalMethodCallsInspection")
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class VltDataService {

    private final VltRepository repository;
    private final VltProperties props;
    private final VersionHashGenerator routeHashGen;
    private final ModelMapper modelMapper;

    private final ConcurrentMap<UUID, VersionBucket> routeCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        routeCache.clear();
        repository.findAllRoutes().stream()
                .map(modelMapper::toRouteId)
                .forEach(this::findAndWriteRouteCacheData);
    }

    @Transactional(readOnly = true)
    public List<Adapter> findAllAdapters() {
        return modelMapper.toAdaptersFromJooq(repository.findAllAdapters());
    }

    @Transactional(readOnly = true)
    public Optional<Adapter> findAdapterById(@NotNull UUID id) {
        return repository.findAdapterById(id).map(modelMapper::toModel);
    }

    @Transactional(readOnly = true)
    public Optional<Adapter> findAdapterByName(@NotEmpty String adapterName) {
        return repository.findAdapterByName(adapterName).map(modelMapper::toModel);
    }

    @Transactional
    public void upsertAdapters(List<AdapterType> adapters) {
        modelMapper.toAdaptersFromType(adapters).stream()
                .map(modelMapper::toJooq)
                .forEach(repository::upsertAdapter);
    }

    @Transactional
    public RouteId createRoute(Route route) {
        var id = repository.insertRoute(modelMapper.toJooq(route));
        var networkNames = route.networks().stream().map(RouteNetwork::name).toList();
        var networkIds = repository.findNetworkIdsByNames(networkNames);
        repository.addNetworksToRoute(networkIds, id);
        return new RouteId(id, routeHashGen.generate());
    }

    @Transactional(readOnly = true)
    public Optional<Route> findRouteById(UUID id) {
        return repository.findRouteById(id)
                .map(it -> {
                    var networks = repository.findRouteNetworksByRouteId(id);
                    return modelMapper.toModel(it, networks);
                });
    }

    public Optional<RouteCacheData> findRouteCacheData(@NotNull RouteId routeId) {
        return getFromCache(routeId.id(), routeId.versionHash());
    }

    @Transactional(readOnly = true)
    public Optional<RouteDefinition> findLatestRouteDefinitionByRouteId(@NotNull UUID routeId) {
        var connections = findNodeConnectionsByRouteId(routeId);
        var nodes = findNodesByRouteId(routeId);

        if (connections.isEmpty() && nodes.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new RouteDefinition(nodes, connections));
    }

    @Transactional
    public void updateRoute(@NotNull Route route) {
        repository.updateRoute(modelMapper.toJooq(route));
        updateRouteNetworks(route.networks(), route.routeId().id());
    }

    @Transactional
    public RouteId updateRouteDefinition(@NotNull RouteId routeId,
                                         List<NodeFullData> nodes,
                                         List<ConnectionFullData> connections) {
        return findRouteCacheData(routeId)
                .map(cache -> { // Если версия уже есть в кэше - не вычисляем новый хэш
                    routeCache.get(routeId.id()).dropNewerThan(
                            routeId.versionHash()); // Удаление кэша более новых версий
                    updateRouteDefinitionInternal(routeId, cache.nodes(),
                                                  cache.connections()); // Приведение БД к прошлой версии
                    return routeId;
                })
                .orElseGet(() -> {
                    updateRouteDefinitionInternal(routeId, nodes, connections); // Приведение БД к новой версии

                    var newVersionHash = routeHashGen.generate();
                    var newRouteId = new RouteId(routeId.id(), newVersionHash);

                    repository.updateRouteVersion(newRouteId.id(), newRouteId.versionHash());
                    findAndWriteRouteCacheData(newRouteId); // Добавление новой версии структуры маршрута в кэш

                    return newRouteId;
                });
    }

    private void updateRouteDefinitionInternal(RouteId routeId, List<NodeFullData> nodes,
                                               List<ConnectionFullData> connections) {
        var nodesToKeep = nodes.stream().map(it -> it.node().id()).toList();
        deleteNodesAndConnectionsFullDataByRouteIdExcluding(routeId.id(), nodesToKeep);

        nodes.forEach(node -> {
            var nodeId = repository.upsertNode(modelMapper.toJooq(node.node(), routeId.id()));
            repository.upsertNodeStyle(modelMapper.toJooq(node.style(), nodeId));
            repository.upsertNodePosition(modelMapper.toJooq(node.position(), nodeId));
        });

        connections.forEach(connection -> {
            var connectionId = repository.insertNodeConnection(modelMapper.toJooq(connection.connection()));
            repository.upsertNodeConnectionStyle(modelMapper.toJooq(connection.style(), connectionId));
        });
    }

    @Transactional(readOnly = true)
    public List<Connection> findNodeConnectionsByRouteId(UUID routeId) {
        return modelMapper.toConnectionsFromJooq(repository.findNodeConnectionsByRouteId(routeId));
    }

    @Transactional(readOnly = true)
    public List<Node> findNodesByRouteId(UUID routeId) {
        return repository.findNodesByRouteId(routeId)
                .stream()
                .map(node -> {
                    var adapter = repository.findAdapterById(node.vltAdapterId());
                    return adapter.map(it -> modelMapper.toModel(node, it))
                            .orElseThrow(() -> {
                                var msg = MessageFormat.format(
                                        "Unable to find adapter for node definition [nodeId: {0}, adapterId: {1}]",
                                        node.id(), node.vltAdapterId());
                                log.error(msg);
                                return new IllegalStateException(msg);
                            });
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NodeFullData> findNodesFullDataByRouteId(RouteId id) {
        return findNodesByRouteId(id.id()).stream()
                .map(it -> {
                    var style = findNodeStyleByNodeId(it.id())
                            .orElseThrow(() -> {
                                var msg = MessageFormat.format("Unable to find node style [nodeId: {0}]", it.id());
                                log.error(msg);
                                return new IllegalStateException(msg);
                            });
                    var position = findNodePositionByNodeId(it.id())
                            .orElseThrow(() -> {
                                var msg = MessageFormat.format("Unable to find node position [nodeId: {0}]", it.id());
                                log.error(msg);
                                return new IllegalStateException(msg);
                            });
                    return new NodeFullData(it, style, position);
                })
                .toList();
    }

    @Transactional
    public void deleteNodesAndConnectionsFullDataByRouteId(UUID routeId) {
        repository.deleteNodeConnectionStylesByRouteId(routeId);
        repository.deleteNodeConnectionsByRouteId(routeId);
        repository.deleteNodePositionsByRouteId(routeId);
        repository.deleteNodeStylesByRouteId(routeId);
        repository.deleteNodesByRouteId(routeId);
    }

    @Transactional
    public void deleteNodesAndConnectionsFullDataByRouteIdExcluding(UUID routeId,
                                                                    List<UUID> toExcludeNodesIds) {
        repository.deleteNodeConnectionStylesFromRouteExcludingNodes(routeId, toExcludeNodesIds);
        repository.deleteNodeConnectionsFromRouteExcludingNodes(routeId, toExcludeNodesIds);
        repository.deleteNodePositionsFromRouteExcluding(routeId, toExcludeNodesIds);
        repository.deleteNodeStylesFromRouteExcluding(routeId, toExcludeNodesIds);
        repository.deleteNodesFromRouteExcluding(routeId, toExcludeNodesIds);
    }

    @Transactional(readOnly = true)
    public Optional<NodePosition> findNodePositionByNodeId(UUID nodeId) {
        return repository.findNodePositionByNodeId(nodeId).map(modelMapper::toModel);
    }

    @Transactional(readOnly = true)
    public Optional<NodeStyle> findNodeStyleByNodeId(UUID nodeId) {
        return repository.findNodeStyleByNodeId(nodeId).map(modelMapper::toModel);
    }

    @Transactional(readOnly = true)
    public List<ConnectionFullData> findNodeConnectionsFullDataByRouteId(RouteId id) {
        return repository.findNodeConnectionsByRouteId(id.id()).stream()
                .map(it -> {
                    var conn = modelMapper.toModel(it);
                    var style = findNodeConnectionStyleByConnectionId(it.id())
                            .orElseThrow(() -> {
                                var msg = MessageFormat.format(
                                        "Unable to find node connection style [connectionId: {0}]", it.id());
                                log.error(msg);
                                return new IllegalStateException(msg);
                            });
                    return new ConnectionFullData(conn, style);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ConnectionStyle> findNodeConnectionStyleByConnectionId(UUID connectionId) {
        return repository.findNodeConnectionStyleByConnectionId(connectionId).map(modelMapper::toModel);
    }

    @Transactional
    public void updateRouteNetworks(List<RouteNetwork> networks, UUID routeId) {
        var actual = repository.findRouteNetworksByRouteId(routeId).stream()
                .map(VltRouteNetwork::name)
                .toList();

        var expected = networks.stream()
                .map(RouteNetwork::name)
                .toList();

        var toAddNames = ListUtils.removeAll(actual, expected);
        var toAddIds = repository.findNetworkIdsByNames(toAddNames);
        repository.addNetworksToRoute(toAddIds, routeId);

        var toRemoveNames = ListUtils.removeAll(expected, actual);
        var toRemoveIds = repository.findNetworkIdsByNames(toRemoveNames);
        repository.removeNetworksFromRoute(toRemoveIds, routeId);
    }

    @Transactional
    public void deleteRouteFullData(@NotNull UUID routeId) {
        deleteNodesAndConnectionsFullDataByRouteId(routeId);
        repository.removeAllNetworksFromRoute(routeId);
        repository.deleteRoute(routeId);

        dropFromCache(routeId);
    }

    private Optional<RouteCacheData> getFromCache(UUID routeId, String versionHash) {
        var bucket = routeCache.getOrDefault(routeId, new VersionBucket(props.getRouteCacheMaxSize()));
        return Optional.ofNullable(bucket.get(versionHash));
    }

    private void dropFromCache(UUID routeId) {
        routeCache.remove(routeId);
    }

    private void putInCache(UUID routeId, String versionHash, RouteCacheData data) {
        var bucket = routeCache.computeIfAbsent(routeId, id -> new VersionBucket(props.getRouteCacheMaxSize()));

        synchronized (bucket) {
            bucket.put(versionHash, data);
        }
    }

    private void findAndWriteRouteCacheData(RouteId id) {
        var nodes = findNodesFullDataByRouteId(id);
        var connections = findNodeConnectionsFullDataByRouteId(id);
        var routeData = new RouteCacheData(nodes, connections);
        putInCache(id.id(), id.versionHash(), routeData);
    }
}
