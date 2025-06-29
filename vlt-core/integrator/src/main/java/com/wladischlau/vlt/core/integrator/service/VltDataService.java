package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.config.properties.VltProperties;
import com.wladischlau.vlt.core.integrator.mapper.ModelMapper;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.Connection;
import com.wladischlau.vlt.core.integrator.model.ConnectionFullData;
import com.wladischlau.vlt.core.integrator.model.ConnectionStyle;
import com.wladischlau.vlt.core.integrator.model.DockerNetwork;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.model.NodeFullData;
import com.wladischlau.vlt.core.integrator.model.NodePosition;
import com.wladischlau.vlt.core.integrator.model.NodeStyle;
import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.integrator.model.RouteCacheData;
import com.wladischlau.vlt.core.integrator.model.RouteDefinition;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.integrator.model.RouteUserAction;
import com.wladischlau.vlt.core.integrator.model.SearchRoutesField;
import com.wladischlau.vlt.core.integrator.model.UserSettings;
import com.wladischlau.vlt.core.integrator.repository.VltRepository;
import com.wladischlau.vlt.core.integrator.utils.VersionBucket;
import com.wladischlau.vlt.core.integrator.utils.VersionHashGenerator;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRoute;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteNetwork;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteUserAction;
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

import static java.util.stream.Collectors.*;

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

    private static final List<String> HIDDEN_ADAPTER_NAMES = List.of("divider");

    private final ConcurrentMap<UUID, VersionBucket> routeCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        routeCache.clear();
        repository.findAllRoutes().stream()
                .map(modelMapper::toRouteId)
                .forEach(this::findAndWriteRouteCacheData);
    }

    public List<Adapter> findAllAdapters() {
        return modelMapper.toAdaptersFromJooq(repository.findAllAdapters());
    }

    public List<Adapter> findInteractableAdapters() {
        return findAllAdapters().stream()
                .filter(it -> !HIDDEN_ADAPTER_NAMES.contains(it.name()))
                .toList();
    }

    public Optional<Adapter> findAdapterById(@NotNull UUID id) {
        return repository.findAdapterById(id).map(modelMapper::toModel);
    }

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
        var routeId = repository.insertRoute(modelMapper.toJooq(route));

        var networkIds = route.networks().stream()
                .map(modelMapper::toJooq)
                .map(it -> {
                    repository.insertRouteNetwork(it);
                    return it.name();
                })
                .collect(collectingAndThen(toList(), repository::findNetworkIdsByNames));

        repository.addNetworksToRoute(networkIds, routeId);
        return new RouteId(routeId, null);
    }

    @Transactional(readOnly = true)
    public List<Route> finaAllRoutes() {
        return toRouteModels(repository.findAllRoutes());
    }

    @Transactional(readOnly = true)
    public Optional<Route> findRouteById(UUID id) {
        return repository.findRouteById(id).map(this::toModel);
    }

    @Transactional(readOnly = true)
    public List<Route> findRoutesByFieldStartsWith(@NotNull SearchRoutesField field,
                                                   @NotEmpty String queryPrefix) {
        var routes = switch (field) {
            case ID -> repository.findRoutesByIdStartsWith(queryPrefix);
            case NAME -> repository.findRoutesByNameStartsWith(queryPrefix);
            case OWNER -> repository.findRoutesByOwnerNameStartsWith(queryPrefix);
        };

        return toRouteModels(routes);
    }

    private List<Route> toRouteModels(List<VltRoute> routes) {
        return routes.stream().map(this::toModel).toList();
    }

    private Route toModel(VltRoute route) {
        var networks = repository.findRouteNetworksByRouteId(route.id());
        return modelMapper.toModel(route, networks);
    }

    public Optional<RouteCacheData> findRouteCacheData(@NotNull RouteId routeId) {
        return getFromCache(routeId.id(), routeId.versionHash());
    }

    public List<String> findRouteCachedVersions(@NotNull UUID routeId) {
        return routeCache.get(routeId).sequencedKeySet().stream().toList();
    }

    public List<RouteUserAction> findAllRouteUserActions() {
        return toRouteUserActionsModel(repository.findAllRouteUserActions());
    }

    public List<RouteUserAction> findRouteUserActionsByUsername(@NotEmpty String username) {
        return toRouteUserActionsModel(repository.findRouteUserActionsByUsername(username));
    }

    private List<RouteUserAction> toRouteUserActionsModel(List<VltRouteUserAction> actions) {
        return actions.stream()
                .map(action -> {
                    return repository.findRouteById(action.vltRouteId())
                            .map(route -> new RouteId(route.id(), route.versionHash()))
                            .map(routeId -> modelMapper.toModel(action, routeId))
                            .orElse(modelMapper.toModel(action, new RouteId(action.vltRouteId(), null)));
                })
                .toList();
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
                    // Удаление кэша более новых версий
                    routeCache.get(routeId.id()).dropNewerThan(routeId.versionHash());
                    // Приведение БД к прошлой версии
                    updateRouteDefinitionInternal(routeId, cache.nodes(), cache.connections());
                    return routeId;
                })
                .orElseGet(() -> {
                    // Приведение БД к новой версии
                    updateRouteDefinitionInternal(routeId, nodes, connections);

                    var newVersionHash = routeHashGen.generate();
                    var newRouteId = new RouteId(routeId.id(), newVersionHash);

                    repository.updateRouteVersion(newRouteId.id(), newRouteId.versionHash());
                    // Добавление новой версии структуры маршрута в кэш
                    findAndWriteRouteCacheData(newRouteId);

                    return newRouteId;
                });
    }

    private void updateRouteDefinitionInternal(RouteId routeId, List<NodeFullData> nodes,
                                               List<ConnectionFullData> connections) {
        deleteNodesAndConnectionsFullDataByRouteId(routeId.id());

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

    public void insertRouteUserAction(@NotNull RouteUserAction routeUserAction) {
        repository.insertRouteUserAction(modelMapper.toJooq(routeUserAction));
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

    public Optional<NodePosition> findNodePositionByNodeId(UUID nodeId) {
        return repository.findNodePositionByNodeId(nodeId).map(modelMapper::toModel);
    }

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

    public Optional<ConnectionStyle> findNodeConnectionStyleByConnectionId(UUID connectionId) {
        return repository.findNodeConnectionStyleByConnectionId(connectionId).map(modelMapper::toModel);
    }

    @Transactional
    public void updateRouteNetworks(List<DockerNetwork> expected, UUID routeId) {
        var actual = modelMapper.toRouteNetworksFromJooq(repository.findRouteNetworksByRouteId(routeId));

        var toAddNames = ListUtils.removeAll(expected, actual).stream()
                .map(modelMapper::toJooq)
                .peek(repository::insertRouteNetwork)
                .map(VltRouteNetwork::name)
                .toList();

        var toAddIds = repository.findNetworkIdsByNames(toAddNames);
        repository.addNetworksToRoute(toAddIds, routeId);

        var toRemoveNames = ListUtils.removeAll(actual, expected).stream()
                .map(DockerNetwork::name)
                .toList();
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

    @Transactional
    public UserSettings findUserSettings(@NotEmpty String username) {
        var settings = repository.findUserSettings(username)
                .orElse(repository.createDefaultUserSettings(username));

        return modelMapper.toModel(settings);
    }

    @Transactional
    public void updateUserSettings(@NotEmpty String username, UserSettings userSettings) {
        repository.updateUserSettings(modelMapper.toJooq(userSettings, username));
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
