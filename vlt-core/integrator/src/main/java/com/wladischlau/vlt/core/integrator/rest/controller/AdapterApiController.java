package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.AdapterApi;
import com.wladischlau.vlt.core.integrator.rest.dto.AdapterDto;
import com.wladischlau.vlt.core.integrator.service.AdapterConfigService;
import com.wladischlau.vlt.core.integrator.service.VltDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@SuppressWarnings("CodeBlock2Expr")
public class AdapterApiController extends ApiController implements AdapterApi {

    private final VltDataService vltDataService;
    private final AdapterConfigService adapterConfigService;

    public AdapterApiController(DtoMapper dtoMapper, VltDataService vltDataService,
                                AdapterConfigService adapterConfigService) {
        super(dtoMapper);
        this.vltDataService = vltDataService;
        this.adapterConfigService = adapterConfigService;
    }

    @Override
    public ResponseEntity<List<AdapterDto>> getAllAdapters(JwtAuthenticationToken principal) {
        return logRequestProcessing(GET_ALL_ADAPTERS, () -> {
            return vltDataService.findAllAdapters().stream()
                    .map(dtoMapper::toDto)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
        });
    }

    @Override
    public ResponseEntity<String> getAdapterConfigSchema(UUID id, JwtAuthenticationToken principal) {
        return logRequestProcessing(GET_ADAPTER_CONFIG_SCHEMA, () -> {
            var schema = adapterConfigService.getAdapterConfigSchemaById(id);
            return ResponseEntity.ok(schema);
        });
    }
}
