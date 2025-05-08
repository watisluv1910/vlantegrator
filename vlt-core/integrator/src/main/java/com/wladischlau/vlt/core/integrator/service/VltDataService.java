package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.mapper.ModelMapper;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.integrator.model.RouteDefinition;
import com.wladischlau.vlt.core.integrator.model.RouteId;
import com.wladischlau.vlt.core.integrator.model.RouteNetwork;
import com.wladischlau.vlt.core.integrator.repository.VltRepository;
import com.wladischlau.vlt.core.integrator.utils.VersionHashGenerator;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltRouteNetwork;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class VltDataService {

    private final VltRepository repository;
    private final VersionHashGenerator routeHashGen;
    private final ModelMapper modelMapper;

    // TODO: Add route nodes cache (not route config itself)

    public List<Adapter> findAllAdapters() {
        return modelMapper.toAdaptersFromJooq(repository.findAllAdapters());
    }

    public Optional<Adapter> findAdapterByName(@NotEmpty String adapterName) {
        return repository.findAdapterByName(adapterName).map(modelMapper::toModel);
    }

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
    public Optional<RouteDefinition> findRouteDefinitionByRouteId(@NotNull UUID routeId) {
        var connections = modelMapper.toConnectionsFromJooq(repository.findNodeConnectionsByRouteId(routeId));
        var nodes = repository.findNodesByRouteId(routeId)
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
        repository.deleteNodeConnectionStylesByRouteId(routeId);
        repository.deleteNodeConnectionsByRouteId(routeId);
        repository.deleteNodePositionsByRouteId(routeId);
        repository.deleteNodeStylesByRouteId(routeId);
        repository.deleteNodesByRouteId(routeId);
        repository.removeAllNetworksFromRoute(routeId);
        repository.deleteRoute(routeId);
    }
}
