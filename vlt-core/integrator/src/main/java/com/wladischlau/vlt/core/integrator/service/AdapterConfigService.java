package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.core.integrator.model.Adapter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdapterConfigService {

    public static final String SCHEMA_CLASSPATH_ROOT = "classpath*:META-INF/adapter-schemas/*.json";
    public static final String ADAPTER_CONFIG_SCHEMA_POSTFIX = "AdapterConfig-schema";

    private final Map<UUID, String> cache = new ConcurrentHashMap<>();

    private final VltDataService vltDataService;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public void init() throws IOException {
        cache.clear();

        for (var res : resolver.getResources(SCHEMA_CLASSPATH_ROOT)) {
            var configDescription = res.getContentAsString(StandardCharsets.UTF_8);
            Optional.ofNullable(res.getFilename())
                    .flatMap(filename -> {
                        int postfixStart = res.getFilename().indexOf(ADAPTER_CONFIG_SCHEMA_POSTFIX);

                        if (postfixStart == -1) {
                            var msg = MessageFormat.format("Adapter config schema resource wrong naming [file: {0}]",
                                                           res.getFilename());
                            log.error(msg);
                            throw new IllegalStateException(msg);
                        }

                        var adapterName = res.getFilename().substring(0, postfixStart);
                        return vltDataService.findAdapterByName(adapterName);
                    })
                    .map(Adapter::id)
                    .ifPresentOrElse(
                            id -> cache.put(id, configDescription),
                            () -> {
                                var msg = MessageFormat.format("Adapter definition not found [resource: {0}]", res);
                                log.error(msg);
                                throw new IllegalStateException(msg);
                            }
                    );
        }
    }

    public String getAdapterConfigSchemaById(UUID id) {
        if (!cache.containsKey(id)) {
            throw new IllegalArgumentException(MessageFormat.format("Adapter config schema not found [id: {0}]", id));
        }

        return cache.get(id);
    }
}
