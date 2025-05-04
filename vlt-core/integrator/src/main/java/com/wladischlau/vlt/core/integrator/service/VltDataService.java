package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.repository.VltRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VltDataService {

    private final VltRepository repo;

    public void upsertAdapters(List<AdapterType> adapters) {
        adapters.forEach(repo::upsertAdapter);
    }
}
