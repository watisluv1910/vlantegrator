package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.mapper.ModelMapper;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.RouteDefinition;
import com.wladischlau.vlt.core.integrator.repository.VltRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class VltDataService {

    private final VltRepository repo;
    private final ModelMapper modelMapper;

    // TODO: Add route cache

    public List<Adapter> findAllAdapters() {
        return modelMapper.toAdaptersFromJooq(repo.findAllAdapters());
    }

    public Optional<Adapter> findAdapterByName(@NotEmpty String adapterName) {
        return repo.findAdapterByName(adapterName).map(modelMapper::toModel);
    }

    public void upsertAdapters(List<AdapterType> adapters) {
        modelMapper.toAdaptersFromType(adapters).stream()
                .map(modelMapper::toJooq)
                .forEach(repo::upsertAdapter);
    }

    @Transactional(readOnly = true)
    public Optional<RouteDefinition> findRouteDefinitionByRouteId(@NotNull UUID routeId) {
        var connections = repo.findNodeConnectionsByRouteId(routeId);
        var nodes = repo.findNodesByRouteId(routeId);

        if (connections.isEmpty() && nodes.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(modelMapper.toRouteDefinition(nodes, connections));
    }
}
