package com.wladischlau.vlt.core.integrator.config;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.service.VltDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdapterInitializer {

    private final VltDataService vltDataService;

    @EventListener(ApplicationReadyEvent.class)
    public void initAdapters() {
        log.info("Starting adapters synchronization...");
        var adapters = Arrays.stream(AdapterType.values()).toList();
        vltDataService.upsertAdapters(adapters);
        log.info("Adapters synchronization completed");
    }
}