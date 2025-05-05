package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.mapper.ModelMapper;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.repository.VltRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VltDataService {

    private final VltRepository repo;
    private final ModelMapper modelMapper;

    public List<Adapter> findAllAdapters() {
        return modelMapper.toAdaptersFromJooq(repo.findAllAdapters());
    }

    public Optional<Adapter> findAdapterByName(String adapterName) {
        return repo.findAdapterByName(adapterName).map(modelMapper::toModel);
    }

    public void upsertAdapters(List<AdapterType> adapters) {
        modelMapper.toAdaptersFromType(adapters).stream()
                .map(modelMapper::toJooq)
                .forEach(repo::upsertAdapter);
    }
}
